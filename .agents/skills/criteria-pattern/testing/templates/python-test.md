# Python Test Template

Using pytest.

## In-Memory Parser Test

```python
import pytest
from criteria import (
    Criteria, SingleFilter, AndFilters, OrFilters, EmptyFilters,
    Filter, FilterOperator, Orders, Order, OrderType
)
from in_memory_parser import InMemoryFederalEntitiesParser
from domain import FederalEntity

class TestInMemoryFederalEntitiesParser:
    parser = InMemoryFederalEntitiesParser()

    records = [
        FederalEntity(id="1", key_code="01", name="Entity One"),
        FederalEntity(id="2", key_code="02", name="Entity Two"),
        FederalEntity(id="3", key_code="03", name="Another Entity"),
    ]

    def test_single_filter_equal_returns_correct_record(self):
        criteria = Criteria(
            filters=SingleFilter(Filter("key_code", FilterOperator.EQUAL, "01")),
            orders=Orders([]),
            limit=None,
            offset=None
        )

        result = self.parser.apply(criteria, self.records)

        assert len(result) == 1
        assert result[0].key_code == "01"

    def test_single_filter_contains_returns_matching_records(self):
        criteria = Criteria(
            filters=SingleFilter(Filter("name", FilterOperator.CONTAINS, "Entity")),
            orders=Orders([]),
            limit=None,
            offset=None
        )

        result = self.parser.apply(criteria, self.records)

        assert len(result) == 3

    def test_and_filters_return_records_matching_all_conditions(self):
        criteria = Criteria(
            filters=AndFilters([
                SingleFilter(Filter("key_code", FilterOperator.EQUAL, "01")),
                SingleFilter(Filter("name", FilterOperator.CONTAINS, "One")),
            ]),
            orders=Orders([]),
            limit=None,
            offset=None
        )

        result = self.parser.apply(criteria, self.records)

        assert len(result) == 1
        assert result[0].key_code == "01"

    def test_or_filters_return_records_matching_any_condition(self):
        criteria = Criteria(
            filters=OrFilters([
                SingleFilter(Filter("key_code", FilterOperator.EQUAL, "01")),
                SingleFilter(Filter("key_code", FilterOperator.EQUAL, "02")),
            ]),
            orders=Orders([]),
            limit=None,
            offset=None
        )

        result = self.parser.apply(criteria, self.records)

        assert len(result) == 2

    def test_order_asc_sorts_correctly(self):
        criteria = Criteria(
            filters=EmptyFilters(),
            orders=Orders([Order("name", OrderType.ASC)]),
            limit=None,
            offset=None
        )

        result = self.parser.apply(criteria, self.records)

        assert [r.name for r in result] == ["Another Entity", "Entity One", "Entity Two"]

    def test_limit_restricts_result_count(self):
        criteria = Criteria(
            filters=EmptyFilters(),
            orders=Orders([]),
            limit=2,
            offset=None
        )

        result = self.parser.apply(criteria, self.records)

        assert len(result) == 2

    def test_offset_skips_records(self):
        criteria = Criteria(
            filters=EmptyFilters(),
            orders=Orders([Order("key_code", OrderType.ASC)]),
            limit=None,
            offset=1
        )

        result = self.parser.apply(criteria, self.records)

        assert result[0].key_code == "02"
```

## Equivalence Test Template

```python
import pytest
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from in_memory_parser import InMemoryFederalEntitiesParser
from sqlalchemy_parser import SQLAlchemyFederalEntitiesParser

class TestCriteriaParserEquivalence:
    @pytest.fixture(autouse=True)
    def setup(self):
        self.engine = create_engine("sqlite:///:memory:")
        self.Session = sessionmaker(bind=self.engine)
        setup_schema(self.engine)
        seed_test_data(self.engine)

        self.in_memory_parser = InMemoryFederalEntitiesParser()
        self.db_parser = SQLAlchemyFederalEntitiesParser(
            self.Session(),
            FederalEntityModel
        )

        yield

        self.Session().close()

    def load_all_records(self):
        session = self.Session()
        records = session.query(FederalEntityModel).all()
        session.close()
        return records

    def test_filter_equivalence_single_equal(self):
        all_records = self.load_all_records()
        criteria = Criteria(
            filters=SingleFilter(Filter("id", FilterOperator.EQUAL, "1")),
            orders=Orders([]),
            limit=1,
            offset=None
        )

        in_memory_result = self.in_memory_parser.apply(criteria, all_records)
        db_result = self.db_parser.matching(criteria)

        assert [r.id for r in in_memory_result] == [r.id for r in db_result]

    def test_filter_equivalence_contains(self):
        all_records = self.load_all_records()
        criteria = Criteria(
            filters=SingleFilter(Filter("name", FilterOperator.CONTAINS, "Entity")),
            orders=Orders([]),
            limit=None,
            offset=None
        )

        in_memory_result = self.in_memory_parser.apply(criteria, all_records)
        db_result = self.db_parser.matching(criteria)

        assert set(r.id for r in in_memory_result) == set(r.id for r in db_result)

    def test_order_equivalence_asc(self):
        all_records = self.load_all_records()
        criteria = Criteria(
            filters=EmptyFilters(),
            orders=Orders([Order("name", OrderType.ASC)]),
            limit=None,
            offset=None
        )

        in_memory_result = self.in_memory_parser.apply(criteria, all_records)
        db_result = self.db_parser.matching(criteria)

        assert [r.name for r in in_memory_result] == [r.name for r in db_result]

    def test_pagination_equivalence_limit(self):
        all_records = self.load_all_records()
        criteria = Criteria(
            filters=EmptyFilters(),
            orders=Orders([Order("id", OrderType.ASC)]),
            limit=2,
            offset=None
        )

        in_memory_result = self.in_memory_parser.apply(criteria, all_records)
        db_result = self.db_parser.matching(criteria)

        assert len(in_memory_result) == len(db_result)
        assert len(in_memory_result) == 2

    def test_complex_criteria_equivalence(self):
        all_records = self.load_all_records()
        criteria = Criteria(
            filters=OrFilters([
                SingleFilter(Filter("key_code", FilterOperator.CONTAINS, "01")),
                SingleFilter(Filter("name", FilterOperator.CONTAINS, "Entity")),
            ]),
            orders=Orders([Order("name", OrderType.ASC)]),
            limit=10,
            offset=0
        )

        in_memory_result = self.in_memory_parser.apply(criteria, all_records)
        db_result = self.db_parser.matching(criteria)

        assert sorted(r.id for r in in_memory_result) == sorted(r.id for r in db_result)
```

## Integration Test Template

```python
import pytest
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy_parser import SQLAlchemyFederalEntitiesParser
from criteria import Criteria, SingleFilter, Filter, FilterOperator, EmptyFilters, Orders

class TestCriteriaIntegration:
    @pytest.fixture(autouse=True)
    def setup(self):
        self.engine = create_engine("sqlite:///:memory:")
        self.Session = sessionmaker(bind=self.engine)
        setup_schema(self.engine)
        seed_test_data(self.engine)

        self.parser = SQLAlchemyFederalEntitiesParser(
            self.Session(),
            FederalEntityModel
        )

        yield

        self.Session().close()

    def test_can_execute_query_without_errors(self):
        criteria = Criteria(
            filters=EmptyFilters(),
            orders=Orders([]),
            limit=None,
            offset=None
        )

        result = self.parser.matching(criteria)

        assert result is not None
        assert len(result) > 0

    def test_count_returns_correct_number(self):
        criteria = Criteria(
            filters=EmptyFilters(),
            orders=Orders([]),
            limit=None,
            offset=None
        )

        count = self.parser.count(criteria)

        assert count == 3

    def test_valid_filter_executes_successfully(self):
        criteria = Criteria(
            filters=SingleFilter(Filter("key_code", FilterOperator.EQUAL, "01")),
            orders=Orders([]),
            limit=None,
            offset=None
        )

        result = self.parser.matching(criteria)

        assert len(result) == 1

    def test_empty_result_returns_empty_list(self):
        criteria = Criteria(
            filters=SingleFilter(Filter("id", FilterOperator.EQUAL, "non-existent")),
            orders=Orders([]),
            limit=None,
            offset=None
        )

        result = self.parser.matching(criteria)

        assert result == []

    def test_invalid_field_handled_gracefully(self):
        criteria = Criteria(
            filters=SingleFilter(Filter("invalid_field", FilterOperator.EQUAL, "value")),
            orders=Orders([]),
            limit=None,
            offset=None
        )

        with pytest.raises(InvalidArgumentError):
            self.parser.matching(criteria)
```

---

Adjust the templates for your specific entity and ORM setup.