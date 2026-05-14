# Python Implementation

## Data Structures

### Enums

```python
from enum import Enum
from dataclasses import dataclass
from typing import Union, List, Optional, Protocol, Any

class FilterOperator(Enum):
    EQUAL = "="
    NOT_EQUAL = "!="
    GT = ">"
    GTE = ">="
    LT = "<"
    LTE = "<="
    CONTAINS = "CONTAINS"
    NOT_CONTAINS = "NOT_CONTAINS"

class OrderType(Enum):
    ASC = "asc"
    DESC = "desc"
    NONE = "none"

class FiltersOperator(Enum):
    AND = "AND"
    OR = "OR"
```

### Filter

```python
@dataclass
class Filter:
    field: str
    operator: FilterOperator
    value: str

    def serialize(self) -> str:
        return f"{self.field}.{self.operator.value}.{self.value}"

    @staticmethod
    def equal(field: str, value: str) -> 'Filter':
        return Filter(field, FilterOperator.EQUAL, value)

    @staticmethod
    def contains(field: str, value: str) -> Filter:
        return Filter(field, FilterOperator.CONTAINS, value)

    # ... other operators
```

### Filters (Boolean Logic)

```python
from abc import ABC, abstractmethod

class Filters(ABC):
    @abstractmethod
    def serialize(self) -> str:
        pass

    @abstractmethod
    def is_empty(self) -> bool:
        pass

    @property
    @abstractmethod
    def is_multiple(self) -> bool:
        pass

class EmptyFilters(Filters):
    def serialize(self) -> str:
        return ""

    def is_empty(self) -> bool:
        return True

    @property
    def is_multiple(self) -> bool:
        return False

class SingleFilter(Filters):
    def __init__(self, filter: Filter):
        self.filter = filter

    def serialize(self) -> str:
        return self.filter.serialize()

    def is_empty(self) -> bool:
        return False

    @property
    def is_multiple(self) -> bool:
        return False

    @staticmethod
    def equal(field: str, value: str) -> 'SingleFilter':
        return SingleFilter(Filter.equal(field, value))

class MultipleFilters(Filters):
    def __init__(self, filters: List[Filters], operator: FiltersOperator):
        self.filters = filters
        self.operator = operator

    def serialize(self) -> str:
        return "^".join(f.serialize() for f in self.filters)

    def is_empty(self) -> bool:
        return len(self.filters) == 0

    @property
    def is_multiple(self) -> bool:
        return True

class AndFilters(MultipleFilters):
    def __init__(self, filters: List[Filters]):
        super().__init__(filters, FiltersOperator.AND)

class OrFilters(MultipleFilters):
    def __init__(self, filters: List[Filters]):
        super().__init__(filters, FiltersOperator.OR)
```

### Order & Orders

```python
@dataclass
class Order:
    order_by: str
    order_type: OrderType

    def has_order(self) -> bool:
        return self.order_type != OrderType.NONE

    def serialize(self) -> str:
        return f"{self.order_by}.{self.order_type.value}"

    @staticmethod
    def from_values(order_by: Optional[str], order_type: Optional[str]) -> 'Order':
        if order_by is None:
            return Order("", OrderType.NONE)
        return Order(order_by, OrderType(order_type.upper() if order_type else "ASC"))

    @staticmethod
    def none() -> 'Order':
        return Order("", OrderType.NONE)

    @staticmethod
    def desc(order_by: str) -> 'Order':
        return Order(order_by, OrderType.DESC)

    @staticmethod
    def asc(order_by: str) -> 'Order':
        return Order(order_by, OrderType.ASC)

@dataclass
class Orders:
    orders: List[Order]

    def __init__(self, orders: List[Order] = None):
        self.orders = orders if orders is not None else []

    def serialize(self) -> str:
        return "^".join(o.serialize() for o in self.orders)

    @staticmethod
    def none() -> 'Orders':
        return Orders([])
```

### Criteria

```python
@dataclass
class Criteria:
    filters: Filters
    orders: Orders
    limit: Optional[int] = None
    offset: Optional[int] = None

    def has_filters(self) -> bool:
        if isinstance(self.filters, SingleFilter):
            return True
        if isinstance(self.filters, MultipleFilters):
            return len(self.filters.filters) > 0
        return False

    def has_orders(self) -> bool:
        return len(self.orders.orders) > 0

    def serialize(self) -> str:
        return f"{self.filters.serialize()}~~{self.orders.serialize()}~~{self.limit or 0}~~{self.offset or 0}"
```

## Domain-Specific Criteria (Example)

```python
class FederalEntityFields:
    ID = "id"
    KEY_CODE = "key_code"
    NAME = "name"
    CREATED_AT = "created_at"
    UPDATED_AT = "updated_at"

class FederalEntityCriteria:
    @staticmethod
    def id_criteria(id: str) -> Criteria:
        return Criteria(
            SingleFilter.equal(FederalEntityFields.ID, id),
            Orders.none(),
            1,
            None
        )

    @staticmethod
    def search_criteria(
        search: Optional[str] = None,
        limit: Optional[int] = None,
        offset: Optional[int] = None
    ) -> Criteria:
        filters = None
        if search:
            filters = OrFilters([
                SingleFilter.contains(FederalEntityFields.KEY_CODE, search),
                SingleFilter.contains(FederalEntityFields.NAME, search),
            ])
        else:
            filters = EmptyFilters()

        return Criteria(
            filters,
            Orders([Order.asc(FederalEntityFields.NAME)]),
            limit,
            offset
        )
```

## Parser Interface

```python
from typing import Protocol, List, TypeVar

T = TypeVar('T')

class CriteriaParser(Protocol):
    def matching(self, criteria: Criteria) -> List[Any]:
        ...

    def count(self, criteria: Criteria) -> int:
        ...
```

## Example Implementation (SQLAlchemy)

```python
from sqlalchemy import select, func, and_, or_
from sqlalchemy.orm import Session

class SQLAlchemyCriteriaParser:
    def __init__(self, session: Session, model: Any, field_map: dict):
        self.session = session
        self.model = model
        self.field_map = field_map  # { logical_field: db_column }

    def matching(self, criteria: Criteria) -> List[Any]:
        query = select(self.model)

        # Apply filters
        if criteria.has_filters():
            conditions = self._build_conditions(criteria.filters)
            if conditions:
                query = query.where(conditions)

        # Apply orders
        if criteria.has_orders():
            order_clauses = []
            for order in criteria.orders.orders:
                column = getattr(self.model, self.field_map.get(order.order_by, order.order_by))
                if order.order_type == OrderType.DESC:
                    order_clauses.append(column.desc())
                elif order.order_type == OrderType.ASC:
                    order_clauses.append(column.asc())
            if order_clauses:
                query = query.order_by(*order_clauses)

        # Apply pagination
        if criteria.limit:
            query = query.limit(criteria.limit)
        if criteria.offset:
            query = query.offset(criteria.offset)

        return list(self.session.execute(query).scalars().all())

    def count(self, criteria: Criteria) -> int:
        query = select(func.count()).select_from(self.model)

        if criteria.has_filters():
            conditions = self._build_conditions(criteria.filters)
            if conditions:
                query = query.where(conditions)

        return self.session.execute(query).scalar()

    def _build_conditions(self, filters: Filters):
        if isinstance(filters, EmptyFilters):
            return None
        if isinstance(filters, SingleFilter):
            return self._filter_to_condition(filters.filter)
        if isinstance(filters, MultipleFilters):
            sub_conditions = [self._build_conditions(f) for f in filters.filters if f]
            if not sub_conditions:
                return None
            if filters.operator == FiltersOperator.AND:
                return and_(*sub_conditions)
            else:
                return or_(*sub_conditions)

    def _filter_to_condition(self, filter: Filter):
        column = getattr(self.model, self.field_map.get(filter.field, filter.field))
        op = filter.operator

        if op == FilterOperator.EQUAL:
            return column == filter.value
        elif op == FilterOperator.NOT_EQUAL:
            return column != filter.value
        elif op == FilterOperator.CONTAINS:
            return column.like(f"%{filter.value}%")
        # ... other operators
```

---

Next: See `testing/strategy.md` for testing approach.