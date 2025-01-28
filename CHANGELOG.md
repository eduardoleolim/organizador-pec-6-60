## [2.3.1](https://github.com/eduardoleolim/organizador-pec-6-60/compare/v2.3.0...v2.3.1) (2025-01-19)

### Bug Fixes

* **instrument:** read instrument file content only during
  registration ([1f92c81](https://github.com/eduardoleolim/organizador-pec-6-60/commit/1f92c810394a6984328cf44756ccc87d774f6cdf))

## [2.3.0](https://github.com/eduardoleolim/organizador-pec-6-60/compare/v2.2.1...v2.3.0) (2024-11-12)

### Features

* allow showing notification badge in navigation
  drawer ([e9c1492](https://github.com/eduardoleolim/organizador-pec-6-60/commit/e9c14929ce8f56bc9a248dc51358b64838e61958))
* **datatable:** implement horizontal
  scrolling ([200d09d](https://github.com/eduardoleolim/organizador-pec-6-60/commit/200d09db9196ef7878cc5f1101e64a3c72d6b37e))
* **home:** allow notification badge in sidebar
  items ([f206073](https://github.com/eduardoleolim/organizador-pec-6-60/commit/f206073d5b230f6b497935b2382a8949b906fef7))
* **instrument:** add error used in saving operations when federal entity does not
  exist ([3fa14dd](https://github.com/eduardoleolim/organizador-pec-6-60/commit/3fa14dd7ba9d3c4f3dd530d7f365551805ad5867))
* **instrument:** add import/export selector
  composable ([93ec260](https://github.com/eduardoleolim/organizador-pec-6-60/commit/93ec260785a3b84c66564e589fed4ead5b70249d))
* **instrument:** add warnings property in
  CanNotImportInstrumentsError ([a9acb70](https://github.com/eduardoleolim/organizador-pec-6-60/commit/a9acb70b1b28154b92828d888eee95320ea717dd))
* **instrument:** format error messages with compose
  resources ([c1b0fae](https://github.com/eduardoleolim/organizador-pec-6-60/commit/c1b0fae6ccd1066e79941151936ddfb382579369))
* **instrument:** implement command for importing records from
  v1 ([a28343e](https://github.com/eduardoleolim/organizador-pec-6-60/commit/a28343e31f9e468c55b6a6da067dbf97a0679b4d))
* **instrument:** implement modal for importing from version
  1.0 ([2250baa](https://github.com/eduardoleolim/organizador-pec-6-60/commit/2250baaba8c00e16eb549b4baa84fdac4d6b4ba3))
* **window:** show "Untitled" as default
  title ([360ed3b](https://github.com/eduardoleolim/organizador-pec-6-60/commit/360ed3b8c76166fdec1901b196279c00035bb2e6))

### Bug Fixes

* **datatable:** reset scroll if scrollbar is not
  visible ([6a3fd24](https://github.com/eduardoleolim/organizador-pec-6-60/commit/6a3fd24514161275c6532624cd6a5a50696a1512))
* **datatable:** set max width of searching
  textfield ([b698345](https://github.com/eduardoleolim/organizador-pec-6-60/commit/b698345bba816ea663c79785ae93ed749961c232))
* **federal-entity:** create record just if it does no exist in importing
  operation ([d16291b](https://github.com/eduardoleolim/organizador-pec-6-60/commit/d16291b14992eb99e63be8b0cb4c392db1de87c5))
## [2.2.1](https://github.com/eduardoleolim/organizador-pec-6-60/compare/v2.2.0...v2.2.1) (2024-09-27)

### Bug Fixes

* specify UUID for Windows installer to ensure proper
  updates ([7867037](https://github.com/eduardoleolim/organizador-pec-6-60/commit/78670377a6ad8d03b9b0f0a0dbf5dbe33f5c636e))
## [2.2.0](https://github.com/eduardoleolim/organizador-pec-6-60/compare/v2.1.0...v2.2.0) (2024-09-26)

### Features

* add scope to error logger
  generator ([98b615f](https://github.com/eduardoleolim/organizador-pec-6-60/commit/98b615f5c34bbdf13346ca576a6e5b4826ad00e9))
* **agency:** implement mutable flow state for search
  functionality ([4e465e6](https://github.com/eduardoleolim/organizador-pec-6-60/commit/4e465e65075c6a0a13cb9ce008df4ab305a5f167))
* **agency:** manage behavior of screen implementing mutable state in screen
  model ([1bdc82d](https://github.com/eduardoleolim/organizador-pec-6-60/commit/1bdc82d17dc46c93673c806875c5309f9d85162b))
* **agency:** show statistic type error
  message ([c47255c](https://github.com/eduardoleolim/organizador-pec-6-60/commit/c47255c2d7ffc8dd5e79fc923a6d8778b18f04f7))
* **agency:** use agency state for
  form ([20d95ca](https://github.com/eduardoleolim/organizador-pec-6-60/commit/20d95ca64fa5927971f246174ebca797cde952ca))
* **auth:** hide login image if window size is
  compact ([749a391](https://github.com/eduardoleolim/organizador-pec-6-60/commit/749a391199c6960edce2948a552a5e8762c27eb9))
* **auth:** show default error message for unknown
  errors ([84798e1](https://github.com/eduardoleolim/organizador-pec-6-60/commit/84798e19551177b94bf8d61af266c6fc857913e5))
* **federal-entity:** implement mutable flow state for search functionality and screen
  management ([b446ea0](https://github.com/eduardoleolim/organizador-pec-6-60/commit/b446ea09fadcdfba4be5a5898bf448dca7b1b2b4))
* **federal-entity:** use federal entity state for
  form ([53b9077](https://github.com/eduardoleolim/organizador-pec-6-60/commit/53b907727b83302043a89693c0c825edc91262b3))
* **instrument:** add callbacks in table to change selected
  filters ([0458c1f](https://github.com/eduardoleolim/organizador-pec-6-60/commit/0458c1f54360e705c0299f8b16945c8de42a0484))
* **instrument:** delete file after delete record from
  database ([21e446e](https://github.com/eduardoleolim/organizador-pec-6-60/commit/21e446e1b5d0399b089e37b83e8836744ab2114e))
* **instrument:** delete instrument just if its status is not saved in
  SIRESO ([4ad5a3a](https://github.com/eduardoleolim/organizador-pec-6-60/commit/4ad5a3a2b18492002c1b789951eecf85d85aaee6))
* **instrument:** implement data mutable state for
  form ([786ca53](https://github.com/eduardoleolim/organizador-pec-6-60/commit/786ca537a7b844d10495b9ec5b09600e7574299e))
* **instrument:** implement state for management of main screen
  operations ([1c001b6](https://github.com/eduardoleolim/organizador-pec-6-60/commit/1c001b6dd02c9ec617a9ba6dd557c4e6e37f2f57))
* **instrument:** reset instrument file picker after save new
  record ([d8350eb](https://github.com/eduardoleolim/organizador-pec-6-60/commit/d8350ebdb72c0fdd5ddefb46139d2a95f8a09d8b))
* **instrument:** reset municipality, agency and statistic type after save new
  record ([2df4cda](https://github.com/eduardoleolim/organizador-pec-6-60/commit/2df4cda13bafa1b7feda9c63c93fac9803c00cf3))
* **instrument:** search instruments after update status in SISERO or delete
  it ([6a135df](https://github.com/eduardoleolim/organizador-pec-6-60/commit/6a135dfa74abc57f24736c2f53b2846b2a28899f))
* **instrument:** show error messages when saving operation
  fails ([69d5d61](https://github.com/eduardoleolim/organizador-pec-6-60/commit/69d5d61524d86cf7c8dec2dd0cf4e67eacfadf1d))
* **instrument:** show error messages when there are empty fileds in
  form ([5d2fd48](https://github.com/eduardoleolim/organizador-pec-6-60/commit/5d2fd48c9aa7bf35f006a7f7514a34e56bc9240a))
* **municipality:** manage crud operations with mutable
  state ([568e26b](https://github.com/eduardoleolim/organizador-pec-6-60/commit/568e26b6e6aea3051a027ce4701f88e584f9bbac))
* **municipality:** manage screen behavior with mutable
  state ([59858ce](https://github.com/eduardoleolim/organizador-pec-6-60/commit/59858ced2dea3f034da764802dab099f28f138d1))
* **municipality:** show error message when deleting request
  fails ([d902927](https://github.com/eduardoleolim/organizador-pec-6-60/commit/d902927c6cd2b580a81bec5e061697aea9c19f61))
* **municipality:** validate if exists agencies associated before delete
  it ([2c0bc1a](https://github.com/eduardoleolim/organizador-pec-6-60/commit/2c0bc1a61efa01dee96433799858799623bf27cb))
* **statistic-type:** implement state for
  form ([b1b7779](https://github.com/eduardoleolim/organizador-pec-6-60/commit/b1b7779b23b954eb036e89876d72069889ef4e6b))
* **statistic-type:** manage screen behavior with mutable
  state ([d1cd969](https://github.com/eduardoleolim/organizador-pec-6-60/commit/d1cd9697b461144fe778ef95a724da0d33c42b2c))
* **statistic-type:** show error message when deleting request
  fails ([9d9b2ec](https://github.com/eduardoleolim/organizador-pec-6-60/commit/9d9b2ec48e1c04a34e801758ee27c1adeed089c9))

### Bug Fixes

* **agency:** keep pagination when reset
  screen ([9b780b2](https://github.com/eduardoleolim/organizador-pec-6-60/commit/9b780b2971d5645d1168b3108b42832f1ae960bb))
* **auth:** clear login
  form ([f924bbb](https://github.com/eduardoleolim/organizador-pec-6-60/commit/f924bbb4e68a39eca0281c157c733216e1f0f3c0))
* **federal-entity:** keep pagination when reset
  screen ([82f6d53](https://github.com/eduardoleolim/organizador-pec-6-60/commit/82f6d534227d4a0db1e9b4199ed2e6c26d748e9d))

### Reverts

* add onSearch callback in federal entity
  and ([d7fd471](https://github.com/eduardoleolim/organizador-pec-6-60/commit/d7fd4712271e504f96e4eff401029a46987e9a7f))
## [2.1.0](https://github.com/eduardoleolim/organizador-pec-6-60/compare/v2.0.0...v2.1.0) (2024-09-17)

### Bug Fixes

* don't allow multiline
  password ([c8e8a55](https://github.com/eduardoleolim/organizador-pec-6-60/commit/c8e8a552bd284602c7ea3202041399ff1387d246))
## [2.0.0](https://github.com/eduardoleolim/organizador-pec-6-60/compare/d673f1deff2cb8b1e979b1aa9de61e5de1bf1bd6...v2.0.0) (2024-09-16)

### Features

* add instrument searcher in koin
  module ([6030b56](https://github.com/eduardoleolim/organizador-pec-6-60/commit/6030b562e956294b58024234ca69e3959aa21624))
* add navigation
  rail ([2ba7609](https://github.com/eduardoleolim/organizador-pec-6-60/commit/2ba76098078a80c0c3ba0aa154fe9e3902afc7b8))
* **agency:** add createdAt and updatedAt
  properties ([1e3b6e5](https://github.com/eduardoleolim/organizador-pec-6-60/commit/1e3b6e5ef08ee7bb04545258ed894726bcaa0bc4))
* **agency:** add federal entity id in response of
  searching ([d1c6ae4](https://github.com/eduardoleolim/organizador-pec-6-60/commit/d1c6ae4c34bf6fb3d19d899f28a84e5077286461))
* **agency:** add view for
  agencies ([309435e](https://github.com/eduardoleolim/organizador-pec-6-60/commit/309435eb8566e7223b5da1027f869c919da1e371))
* **agency:** delete
  record ([b284155](https://github.com/eduardoleolim/organizador-pec-6-60/commit/b2841554a8c8a09515e58960487eeb338c298543))
* **agency:** delete record with
  form ([9382aa8](https://github.com/eduardoleolim/organizador-pec-6-60/commit/9382aa82ae89e628dc20cdba0ef85e44b920c116))
* **agency:** implement domain
  entity ([556f864](https://github.com/eduardoleolim/organizador-pec-6-60/commit/556f864b26832f498292d4f64f1967c913ce94df))
* **agency:** implement repository with
  ktorm ([22cfae5](https://github.com/eduardoleolim/organizador-pec-6-60/commit/22cfae54344203552e2a669190cf947325deced5))
* **agency:** implement service to create
  records ([a0292df](https://github.com/eduardoleolim/organizador-pec-6-60/commit/a0292dff09fe78500f94ecb7617a72b329dc84dd))
* **agency:** load query handlers in
  bus ([52ef98d](https://github.com/eduardoleolim/organizador-pec-6-60/commit/52ef98d31f4ce92bcced766a3f0719955ba1d585))
* **agency:** search agencies of
  municipality ([f794b7d](https://github.com/eduardoleolim/organizador-pec-6-60/commit/f794b7da3aa512ea2888f77720791904e4930c90))
* **agency:** search record by
  id ([f90c17c](https://github.com/eduardoleolim/organizador-pec-6-60/commit/f90c17cbac2e025c5c7ea97190629eb413aa87b5))
* **agency:** search record by name or keyCode of
  municipality ([8661e4f](https://github.com/eduardoleolim/organizador-pec-6-60/commit/8661e4f45db9029c28b90d283d20c1b6d7edd68f))
* **agency:** search records by
  term ([636b01c](https://github.com/eduardoleolim/organizador-pec-6-60/commit/636b01c9acdf0a636fba64f31a98768ef8770cc4))
* **agency:** sort statistic types by
  keyCode ([d3d87c9](https://github.com/eduardoleolim/organizador-pec-6-60/commit/d3d87c9e4d856ee023c994d4c9c0074dc7f86f98))
* **agency:** update
  record ([f20d3cc](https://github.com/eduardoleolim/organizador-pec-6-60/commit/f20d3cc21b51920ccc84f38c261a93ec51e346f6))
* **agency:** update record with
  form ([35e5854](https://github.com/eduardoleolim/organizador-pec-6-60/commit/35e5854fb780e718f9a3ddbbb83fa7ae481c9d1a))
* **agency:** view for searching of saving of
  records ([e5f9984](https://github.com/eduardoleolim/organizador-pec-6-60/commit/e5f99847b315a3b37dfb9f2aace38d4a836ef556))
* **app:** add temp directory in config
  file ([bd3c22d](https://github.com/eduardoleolim/organizador-pec-6-60/commit/bd3c22d329762650082bbfbedcdd992dad3b32b0))
* **app:** add theme switcher to
  titlebar ([4d6ad69](https://github.com/eduardoleolim/organizador-pec-6-60/commit/4d6ad691693f3932eb8b46d9e09297fe7d203c9f))
* **app:** calculate data
  directory ([3cc4a28](https://github.com/eduardoleolim/organizador-pec-6-60/commit/3cc4a28c666b2dd4a7960a6f502adeed98b363eb))
* **app:** define color
  schemas ([892026d](https://github.com/eduardoleolim/organizador-pec-6-60/commit/892026dcacf37de6e20cdb0302c2a21763651e05))
* **app:** define custom dialog
  windows ([56abe5c](https://github.com/eduardoleolim/organizador-pec-6-60/commit/56abe5cd79608363ae1027200a7f02f3bcd51de5))
* **app:** implement app theme
  composable ([513d23c](https://github.com/eduardoleolim/organizador-pec-6-60/commit/513d23cdc4b7a5d8170865618f2ca56b63a1271f))
* **app:** implement decorated window using JBR
  api ([19309ca](https://github.com/eduardoleolim/organizador-pec-6-60/commit/19309cac083a0b117a10c991322b14d3bc6cfc3f))
* **app:** implement dialog
  window ([799ec94](https://github.com/eduardoleolim/organizador-pec-6-60/commit/799ec9404d1a6899e9cb39dea8419ddbbd019f6d))
* **app:** implement JBR api for customize the
  titlebar ([c4d25d7](https://github.com/eduardoleolim/organizador-pec-6-60/commit/c4d25d7931228dad1c8bbbf6666a73e29b9277b5))
* **app:** implement titlebar for
  macos ([36bd6e6](https://github.com/eduardoleolim/organizador-pec-6-60/commit/36bd6e693acdaac68239e5609f4cdef53a578729))
* **app:** load text in init screen from
  resources ([37d79b3](https://github.com/eduardoleolim/organizador-pec-6-60/commit/37d79b3be8dbaff5a967086869103acbab501ac4))
* **app:** read properties with properlty
  library ([81edc9d](https://github.com/eduardoleolim/organizador-pec-6-60/commit/81edc9da2c5e62692a7e70ceb1f2fa13ab02ace8))
* **app:** resize window according
  density ([689989e](https://github.com/eduardoleolim/organizador-pec-6-60/commit/689989ecf5074968708fb8a0956e1e7241ebdf00))
* **app:** translate to
  spanish ([f384818](https://github.com/eduardoleolim/organizador-pec-6-60/commit/f384818f0f33a95644b3762acd30b1bd5c065f4b))
* **auth:** add query handlers to
  bus ([08d4d6d](https://github.com/eduardoleolim/organizador-pec-6-60/commit/08d4d6d857894456889aa83f003a7f260565e841))
* **auth:** authenticate
  user ([34375a0](https://github.com/eduardoleolim/organizador-pec-6-60/commit/34375a0338dbefcd793f94deda679497c384cda8))
* **auth:** authenticate user by
  query ([5f9a7d9](https://github.com/eduardoleolim/organizador-pec-6-60/commit/5f9a7d9e2c3db05d1f61efe8fac2dc54afeb2eda))
* **auth:** authenticate using query
  bus ([c521e61](https://github.com/eduardoleolim/organizador-pec-6-60/commit/c521e61fce7107da1d9d1a59368dca27d191d404))
* **auth:** define auth
  screen ([70d86d8](https://github.com/eduardoleolim/organizador-pec-6-60/commit/70d86d87d4d2208c3f7494d689b4132e1a8640df))
* **auth:** implement auth repository with
  ktorm ([a45eb6f](https://github.com/eduardoleolim/organizador-pec-6-60/commit/a45eb6f57e8b9b2e1f9e04a72c8fd657403daf19))
* **auth:** implement states for
  login ([23a3ae1](https://github.com/eduardoleolim/organizador-pec-6-60/commit/23a3ae175be7a04b16cd5a4e68e82f636e4bf699))
* **auth:** show circle progress
  indicator ([ad75843](https://github.com/eduardoleolim/organizador-pec-6-60/commit/ad7584350b22d5dea628fa2bc4ab8bc7fb3bad02))
* **auth:** update GUI based on
  errors ([c9b397b](https://github.com/eduardoleolim/organizador-pec-6-60/commit/c9b397b615834649ce952beeac328fd2524860d6))
* avoid invalid
  filters ([63ac228](https://github.com/eduardoleolim/organizador-pec-6-60/commit/63ac22860daa9e0307bfd4564c5621f4d0fc5438))
* change typography for Roboto
  font ([a480cd2](https://github.com/eduardoleolim/organizador-pec-6-60/commit/a480cd2dc024285c4e11b4e4dcbae86b9a03576f))
* **component:** datatable with
  pagination ([bb45703](https://github.com/eduardoleolim/organizador-pec-6-60/commit/bb45703a3923e13de922a9a70b49fd1b354067b8))
* **components:** implement composable
  table ([818dc1a](https://github.com/eduardoleolim/organizador-pec-6-60/commit/818dc1adb7752913566451e99258e909137b01cf))
* **components:** show error
  dialogs ([c2c1344](https://github.com/eduardoleolim/organizador-pec-6-60/commit/c2c13443ece68c532757e44edec38942f4432382))
* **composable:** add validation in
  OutlinedSelect ([5e304e3](https://github.com/eduardoleolim/organizador-pec-6-60/commit/5e304e322cdbc26c833a79ae06b973668e02172c))
* **composable:** allow change text of
  OutlinedFilePicker ([3c88af4](https://github.com/eduardoleolim/organizador-pec-6-60/commit/3c88af48f69f52146dea6a12648465c3d82b15b7))
* **composable:** implement outlined file
  picker ([61424e1](https://github.com/eduardoleolim/organizador-pec-6-60/commit/61424e1f6fbfa8cd7940d10ff848d02461426cc5))
* **composable:** implement pdf
  viewer ([6aaa28c](https://github.com/eduardoleolim/organizador-pec-6-60/commit/6aaa28c8549b326b4e3b2f2b9e37c7775b2513a4))
* **composable:** reset OutlinedFilePicker with
  interactions ([ce885e9](https://github.com/eduardoleolim/organizador-pec-6-60/commit/ce885e9eb4a5d7bdee532a1c25ff4bc87f8ffa33))
* **composable:** set default filter in
  OutlinedFilePicker ([e8f222d](https://github.com/eduardoleolim/organizador-pec-6-60/commit/e8f222d94733a7bd3f4427b17a7fae928e78d6d6))
* config app
  initialization ([6097ecc](https://github.com/eduardoleolim/organizador-pec-6-60/commit/6097ecc0532023d4648daca6bfb01745577dad5b))
* **config:** load config file from root config
  directory ([75abd58](https://github.com/eduardoleolim/organizador-pec-6-60/commit/75abd584ed3c5602bf14ae4f33077b40de615a63))
* convert Date to
  LocalDateTime ([2506536](https://github.com/eduardoleolim/organizador-pec-6-60/commit/250653687e39bea29ef839e4190d81fbd42dd74d))
* **core:** export auth application
  packages ([a67f379](https://github.com/eduardoleolim/organizador-pec-6-60/commit/a67f379e0d81ac7fde5fe033eae6b53b192d12be))
* create ktorm database
  model ([f089a51](https://github.com/eduardoleolim/organizador-pec-6-60/commit/f089a512daa08c43faf5d5c5a5c3e0fa3c3ca3df))
* **criteria:** filter orders according
  fields ([5aa5ba9](https://github.com/eduardoleolim/organizador-pec-6-60/commit/5aa5ba9873e76eeafab2b5ae7b267d6830cb607f))
* **criteria:** support many
  orders ([e15b79b](https://github.com/eduardoleolim/organizador-pec-6-60/commit/e15b79bc084ac83334ea35d7ce978fcd7b743d9e))
* **database:** add agencyId column in instrument
  table ([462d81b](https://github.com/eduardoleolim/organizador-pec-6-60/commit/462d81b084652be545a7ef05ed592014947d6742))
* **database:** add createdAt and updatedAt
  columns ([1a18bc9](https://github.com/eduardoleolim/organizador-pec-6-60/commit/1a18bc9d61bff9092638e6569c9dad52c49f8782))
* **database:** add unicode
  plugin ([783b7df](https://github.com/eduardoleolim/organizador-pec-6-60/commit/783b7dfdfa26ca156f779c91a89a04de80c63bad))
* **database:** check if database schema
  exists ([19d8be8](https://github.com/eduardoleolim/organizador-pec-6-60/commit/19d8be8585e072329aa79c0887077fdb83714ceb))
* **database:** create database if not
  exists ([4ca72c0](https://github.com/eduardoleolim/organizador-pec-6-60/commit/4ca72c0223f104945f420456dd8fcf363312404b))
* **database:** define
  model ([32401ce](https://github.com/eduardoleolim/organizador-pec-6-60/commit/32401ce92f51f0726ed2c98ea2f2d3cbd2d31454))
* **database:** implement model for
  agency ([ce971e0](https://github.com/eduardoleolim/organizador-pec-6-60/commit/ce971e05bc21637cf744dc2d65f817adcdad558c))
* **database:** load sqlite extensions in bus and encrypt
  database ([222031e](https://github.com/eduardoleolim/organizador-pec-6-60/commit/222031eb71135f3d36be4c3f797db5995b4b0213))
* **database:** load sqlite extensions in bus and encrypt
  database ([5cb4d29](https://github.com/eduardoleolim/organizador-pec-6-60/commit/5cb4d292adda2e78780be2f9f4b1837562ff2329))
* **database:** normalize instrument
  table ([2f0cf30](https://github.com/eduardoleolim/organizador-pec-6-60/commit/2f0cf304ff9b313fc15393bb8b24472222bfa3ad))
* **database:** save path of
  instrument ([e5488ea](https://github.com/eduardoleolim/organizador-pec-6-60/commit/e5488eacc3579f2e4dad223b72272e10d48ddec4))
* **datatable:** add scrollbar in
  header ([c49f524](https://github.com/eduardoleolim/organizador-pec-6-60/commit/c49f524f977fe3c153c84ee0d17a2f6301a4b738))
* **datatable:** load text values from
  resource ([2fe4184](https://github.com/eduardoleolim/organizador-pec-6-60/commit/2fe418414a17f60770f51805b12110deb27510cc))
* **dialog:** load text values from
  resource ([8d1a8d5](https://github.com/eduardoleolim/organizador-pec-6-60/commit/8d1a8d5c054114639760af9bca143d767e1dd1fd))
* **dialog:** modal dialog for
  questions ([3b92b70](https://github.com/eduardoleolim/organizador-pec-6-60/commit/3b92b7069f35fe21a1208ab8fe1858f92fdf54e0))
* **either:** implement async
  fold ([7b9e41f](https://github.com/eduardoleolim/organizador-pec-6-60/commit/7b9e41f01c561ad1168b12b27194a23144905437))
* **federal-entity:** add criteria for update
  validations ([39c7265](https://github.com/eduardoleolim/organizador-pec-6-60/commit/39c72656221673a6105aa491cb2a18019f2357b6))
* **federal-entity:** add queries to
  bus ([bcaad95](https://github.com/eduardoleolim/organizador-pec-6-60/commit/bcaad95cb5093f91ae484435cec4da100c4a2231))
* **federal-entity:** add table for
  management ([be65b3b](https://github.com/eduardoleolim/organizador-pec-6-60/commit/be65b3bab00e16572fbb329a69d26d91b5653d19))
* **federal-entity:** count total
  records ([ceeb5f8](https://github.com/eduardoleolim/organizador-pec-6-60/commit/ceeb5f85ba1f2e2e1bd3b6bea78252012ee09268))
* **federal-entity:** create, update and delete
  record ([d536bc4](https://github.com/eduardoleolim/organizador-pec-6-60/commit/d536bc4d23caa1476260c4125ebc90ab5368cac7))
* **federal-entity:** delete record with
  command ([3349bf9](https://github.com/eduardoleolim/organizador-pec-6-60/commit/3349bf94cdca3220df49a39a6232c3bc23def7f6))
* **federal-entity:** find records by
  term ([9dacfa4](https://github.com/eduardoleolim/organizador-pec-6-60/commit/9dacfa48146aac5604ca4de66a7da76db8ec7945))
* **federal-entity:** form to create and update
  record ([13c0e3a](https://github.com/eduardoleolim/organizador-pec-6-60/commit/13c0e3a4edf3822088848433a5f590b45c6475d3))
* **federal-entity:** implement command handler for importing of
  records ([78fdd78](https://github.com/eduardoleolim/organizador-pec-6-60/commit/78fdd78be693b221e5a266b5012e907f6d465b8c))
* **federal-entity:** implement command to
  create ([f8be36b](https://github.com/eduardoleolim/organizador-pec-6-60/commit/f8be36bf130090fcea9343b2875d3d4df415d31b))
* **federal-entity:** implement command to
  update ([d06189e](https://github.com/eduardoleolim/organizador-pec-6-60/commit/d06189e2adb077e8230a7e5f4086c1eed952f52a))
* **federal-entity:** implement fields for criteria and
  parser ([5468d7a](https://github.com/eduardoleolim/organizador-pec-6-60/commit/5468d7a501f4d1a76c800f5c2d7c5f4e6a2688e8))
* **federal-entity:** implement repository with
  ktorm ([b55f5d8](https://github.com/eduardoleolim/organizador-pec-6-60/commit/b55f5d8ad2a0bcb2aef6953c2ab6ce2577fe4996))
* **federal-entity:** implement states for crud
  operations ([2cdc363](https://github.com/eduardoleolim/organizador-pec-6-60/commit/2cdc363524a2695ce7fc8bd35a84615010545ab0))
* **federal-entity:** import records from
  csv ([53e98bd](https://github.com/eduardoleolim/organizador-pec-6-60/commit/53e98bd122eb2574989b55a4cd60997557054bc2))
* **federal-entity:** include metadata in searching
  response ([25538ae](https://github.com/eduardoleolim/organizador-pec-6-60/commit/25538aeb547b2366da248608e194424b96b150f4))
* **federal-entity:** interface to delete and search
  records ([06bba36](https://github.com/eduardoleolim/organizador-pec-6-60/commit/06bba36da3c296b94f3a381e8182d5cb48e6eadd))
* **federal-entity:** load command for importing in
  bus ([29b777c](https://github.com/eduardoleolim/organizador-pec-6-60/commit/29b777c509e4e7f5120bac323a5cbdb0d6b9b19b))
* **federal-entity:** load text values from
  resource ([69f1d39](https://github.com/eduardoleolim/organizador-pec-6-60/commit/69f1d3953885d9a8dcc82942a0a3716846c260ec))
* **federal-entity:** search by
  id ([a4d6e28](https://github.com/eduardoleolim/organizador-pec-6-60/commit/a4d6e289ae6a300a00a988b6109f1d38c14d3def))
* **federal-entity:** set sizes of table's
  columns ([06aa1d0](https://github.com/eduardoleolim/organizador-pec-6-60/commit/06aa1d0e1f810cca20caa9c60fad9c3c4559a4e8))
* **federal-entity:** show error message in
  form ([ab7bd50](https://github.com/eduardoleolim/organizador-pec-6-60/commit/ab7bd50d26496b08cf5fba6d784fc5e2dbe93b94))
* **federal-entity:** show error message when try to delete a
  record ([baf55ee](https://github.com/eduardoleolim/organizador-pec-6-60/commit/baf55ee2c3a2dc932e9418b44b68e9bad7d91e2c))
* **federal-entity:** show error messages in
  form ([92b9d22](https://github.com/eduardoleolim/organizador-pec-6-60/commit/92b9d224fdb5be35a52b307c56d660cd54a97631))
* **federal-entity:** show records with
  view ([f4e9faa](https://github.com/eduardoleolim/organizador-pec-6-60/commit/f4e9faa017ce1401e2f149ef3e977d3678e99950))
* **federal-entity:** show text from strings of
  resources ([25699e5](https://github.com/eduardoleolim/organizador-pec-6-60/commit/25699e54449bbbdba44beb126b4f09f524513ab3))
* **federal-entity:** show tooltip on icon
  buttons ([b07485a](https://github.com/eduardoleolim/organizador-pec-6-60/commit/b07485aa398342bfd8e8b5fefff9680bbefe0c9c))
* **federal-entity:** take default value for
  updateAt ([c5fa1e4](https://github.com/eduardoleolim/organizador-pec-6-60/commit/c5fa1e423ee1be52ec5a52f926170f62a793c6ec))
* **federal-entity:** use import command in view
  model ([3fb07d7](https://github.com/eduardoleolim/organizador-pec-6-60/commit/3fb07d72c961fb28216185170945db65d5873ae2))
* **federal-entity:** use ktorm
  entity ([4ac5fc6](https://github.com/eduardoleolim/organizador-pec-6-60/commit/4ac5fc68a58c2612f0c0a1ca5a9f5a27fbec3133))
* **federal-entity:** validate before
  create ([d369ac1](https://github.com/eduardoleolim/organizador-pec-6-60/commit/d369ac128c49fd29d6a8beb221d4af2a2b28f028))
* **federal-entity:** validate if has any municipalities
  associated ([600f576](https://github.com/eduardoleolim/organizador-pec-6-60/commit/600f5762fa96380b929c5352e1dc02e5e3dae7fd))
* **forms:** validate if field is empty not
  blank ([9c1b530](https://github.com/eduardoleolim/organizador-pec-6-60/commit/9c1b530f7df40e7e6d8a0d5814ae6b3bbfe1d0c2))
* generate a log when an error
  occurs ([3f14ba1](https://github.com/eduardoleolim/organizador-pec-6-60/commit/3f14ba1fd83fb20c876822cb5b779f335f7d1706))
* get database path from cli
  arguments ([a8b29cd](https://github.com/eduardoleolim/organizador-pec-6-60/commit/a8b29cd0dca22851c51c50d790a7d1024335b7ff))
* **home:** add menu and
  navigation ([4d4075a](https://github.com/eduardoleolim/organizador-pec-6-60/commit/4d4075a27f8ce31c2d9e04719691df1cec1c422f))
* **home:** add title to
  menu ([5b632cd](https://github.com/eduardoleolim/organizador-pec-6-60/commit/5b632cd7f951232b95031b89715d99941e2d3e9f))
* **home:** add
  transitions ([42fe726](https://github.com/eduardoleolim/organizador-pec-6-60/commit/42fe726e350c00cc6e30b9900e80af19bca25560))
* **home:** animate switch of selected
  tab ([61af756](https://github.com/eduardoleolim/organizador-pec-6-60/commit/61af7569afd7824a03b7ca70f4aa1e04ee94d3f3))
* **home:** custom topBar with composition
  provider ([5fd9cd0](https://github.com/eduardoleolim/organizador-pec-6-60/commit/5fd9cd0fbaced0f5f61b1daf6597cf29f46435ea))
* **home:** define home
  screen ([80865cb](https://github.com/eduardoleolim/organizador-pec-6-60/commit/80865cbdd9cb8ca3bea30da58d1a5cf5465699f4))
* **home:** inject user data in home
  view ([e80857a](https://github.com/eduardoleolim/organizador-pec-6-60/commit/e80857a218d198be35b5e24657739a7904981467))
* **home:** set minimum window's
  size ([14d03b7](https://github.com/eduardoleolim/organizador-pec-6-60/commit/14d03b7156e772bae0a296674dae7a8e3591e29a))
* **home:** show text from strings of
  resources ([70ac86d](https://github.com/eduardoleolim/organizador-pec-6-60/commit/70ac86d019d2e057532ac0b7cdd93a2a6e2aa54c))
* **home:** switch menu between medium and expanded
  screen ([7592fdc](https://github.com/eduardoleolim/organizador-pec-6-60/commit/7592fdcb1cb90522eb8ec793b8dec562a0fad961))
* implement command bus with
  ktorm ([c2f9f5f](https://github.com/eduardoleolim/organizador-pec-6-60/commit/c2f9f5f3c652bb74cc7794bc02b2ba47680c05ec))
* implement dialog
  window ([89cd7dc](https://github.com/eduardoleolim/organizador-pec-6-60/commit/89cd7dcc3fa703647a2083dd77aca9b7f11454e3))
* implement
  Either ([c21de88](https://github.com/eduardoleolim/organizador-pec-6-60/commit/c21de8895f1e72253a5823d690b132bd77d65a8a))
* implement outlined-select
  composable ([2bc9048](https://github.com/eduardoleolim/organizador-pec-6-60/commit/2bc904866c16dfaa92a48f8057ee40bc8efa2e6f))
* implement query bus with
  ktorm ([2422e4b](https://github.com/eduardoleolim/organizador-pec-6-60/commit/2422e4b3edcc0384ed2c6b2c5f409dab35d12402))
* implement searching
  list ([afff46d](https://github.com/eduardoleolim/organizador-pec-6-60/commit/afff46db843fe7b2273986ef2cad8380aa993a7d))
* implement title bar for
  linux ([a43f75c](https://github.com/eduardoleolim/organizador-pec-6-60/commit/a43f75cdfd15bf96866b2b2427b96d104854f4db))
* implement util for checking of current
  platform ([94af90c](https://github.com/eduardoleolim/organizador-pec-6-60/commit/94af90c0825a4b91b205257f77463f5b3cd08dd0))
* include index parameter in
  onValueSelected ([6b91402](https://github.com/eduardoleolim/organizador-pec-6-60/commit/6b914021b7d25143ba53c40172161e67ecbe6729))
* **instrument-type:** add metadata in response of
  searching ([e658a8e](https://github.com/eduardoleolim/organizador-pec-6-60/commit/e658a8e470fb4f5d4fc23b20c5d568ab76d11e37))
* **instrument-type:** add states for crud operations in
  view-model ([abb3f9a](https://github.com/eduardoleolim/organizador-pec-6-60/commit/abb3f9a4b44067fb21b889f356f1884eae5c6279))
* **instrument-type:** create
  record ([2566eea](https://github.com/eduardoleolim/organizador-pec-6-60/commit/2566eea6fe3a96b222766f3315e48da535bad559))
* **instrument-type:** create record with
  command ([319a79d](https://github.com/eduardoleolim/organizador-pec-6-60/commit/319a79ddb3a4fedddd0719489e2fe71f75b5017c))
* **instrument-type:** delete record with
  command ([5ee0ddb](https://github.com/eduardoleolim/organizador-pec-6-60/commit/5ee0ddb3000325938bfaef753f49c92b313af5ce))
* **instrument-type:** forms for deleting and
  updating ([86e8bed](https://github.com/eduardoleolim/organizador-pec-6-60/commit/86e8bedb3f4b2018af9eaf90dace3296d6d1b842))
* **instrument-type:** implement base
  view ([9b32d27](https://github.com/eduardoleolim/organizador-pec-6-60/commit/9b32d2742145b09e5f8a7eaedf53137571c69f48))
* **instrument-type:** implement repository with
  ktorm ([efabfc6](https://github.com/eduardoleolim/organizador-pec-6-60/commit/efabfc68397be3742416ae7206e61949e69c9bc6))
* **instrument-type:** load commands in command
  bus ([671612b](https://github.com/eduardoleolim/organizador-pec-6-60/commit/671612b1f94d922c2f81e1498cafc6723c0a3d1d))
* **instrument-type:** load queries in query
  bus ([7d2cef1](https://github.com/eduardoleolim/organizador-pec-6-60/commit/7d2cef1fb957688cc8895fd6857c0cccce62810d))
* **instrument-type:** load text values from
  resource ([d9eec98](https://github.com/eduardoleolim/organizador-pec-6-60/commit/d9eec98b289841d9936c788a52a9c8d62f27ece4))
* **instrument-type:** search record by id with
  query ([f2097a8](https://github.com/eduardoleolim/organizador-pec-6-60/commit/f2097a8ac6430a0b4bd68d838355ef676b62e30e))
* **instrument-type:** search records and show them in
  table ([693156a](https://github.com/eduardoleolim/organizador-pec-6-60/commit/693156af0797edb25f2ceb434d64fe6fb0917f7a))
* **instrument-type:** search records by a term with a
  query ([301b417](https://github.com/eduardoleolim/organizador-pec-6-60/commit/301b417b188dc8a7ee95bf5517f3bf881d9cbab9))
* **instrument-type:** update
  record ([ee87397](https://github.com/eduardoleolim/organizador-pec-6-60/commit/ee873974e0cbd2ab56ce98d54c0b10fc21d22232))
* **instrument-type:** update record with
  command ([820db28](https://github.com/eduardoleolim/organizador-pec-6-60/commit/820db281fb5177c18a546ea583609cabe98e806b))
* **instrument:** add callback in table for show details and copy to
  clipboard ([ce2fef8](https://github.com/eduardoleolim/organizador-pec-6-60/commit/ce2fef84d845f7a1ca422390a45a172bcd16f657))
* **instrument:** add commands in
  bus ([4f39770](https://github.com/eduardoleolim/organizador-pec-6-60/commit/4f3977008c791b56e043542442064d13b3852467))
* **instrument:** add filters in
  table ([2e859d6](https://github.com/eduardoleolim/organizador-pec-6-60/commit/2e859d60e224e4ddc38f317c4ddd3e1b9ab17de0))
* **instrument:** add getter for filename of
  document ([4bc3ea6](https://github.com/eduardoleolim/organizador-pec-6-60/commit/4bc3ea6fd5ccb744cfce9b367a6b5afdf08786ed))
* **instrument:** add handler of searcher by id to
  bus ([842006a](https://github.com/eduardoleolim/organizador-pec-6-60/commit/842006aa464017781510e4f5e7024e9705eab1ef))
* **instrument:** add instrument field for criteria of
  searching ([dfee96f](https://github.com/eduardoleolim/organizador-pec-6-60/commit/dfee96f8c92322323b7a95bcf10a2664e265dfd1))
* **instrument:** add instrument id parameter in form
  screen ([2ea4508](https://github.com/eduardoleolim/organizador-pec-6-60/commit/2ea4508341171345bb7b29dc96f6b17c780e8a36))
* **instrument:** add instrument
  view ([791911e](https://github.com/eduardoleolim/organizador-pec-6-60/commit/791911eb69bd58eeaee40a684fb6ca4f76e235d8))
* **instrument:** add instruments path
  property ([e88ab8b](https://github.com/eduardoleolim/organizador-pec-6-60/commit/e88ab8b55bfe51973806e1f3cbb735e70272c3db))
* **instrument:** add screen for saving of new
  records ([b124cc3](https://github.com/eduardoleolim/organizador-pec-6-60/commit/b124cc3a3efb764435577da88c0f3537a36d76cb))
* **instrument:** change instrument
  file ([2e3f6b1](https://github.com/eduardoleolim/organizador-pec-6-60/commit/2e3f6b1aa063d8c0e18325773f89e153674c7147))
* **instrument:** check if agency exists before save
  record ([3cd5da2](https://github.com/eduardoleolim/organizador-pec-6-60/commit/3cd5da232ecfc9c632ca9d32334c126e2637c61e))
* **instrument:** check if instrument exists before save new
  record ([79bf7e1](https://github.com/eduardoleolim/organizador-pec-6-60/commit/79bf7e174488b3930c5e3d8425a90bad55d93205))
* **instrument:** copy instrument to
  clipboard ([33a0f32](https://github.com/eduardoleolim/organizador-pec-6-60/commit/33a0f32fe34e62da37565834fe0398a5e2e90ee2))
* **instrument:** create
  instrument ([d96c79f](https://github.com/eduardoleolim/organizador-pec-6-60/commit/d96c79f670cfbbf355d93c469e76eb9223034035))
* **instrument:** delete file when saving of new record
  fails ([b13a909](https://github.com/eduardoleolim/organizador-pec-6-60/commit/b13a9096fc42a15452ff3ba99945fab7289c0f30))
* **instrument:** delete instrument from
  table ([6c1ce9d](https://github.com/eduardoleolim/organizador-pec-6-60/commit/6c1ce9d3237aa11e08ed6f251bd8b95f766a81b0))
* **instrument:** implement command handler for
  deleting ([b8a6fbf](https://github.com/eduardoleolim/organizador-pec-6-60/commit/b8a6fbf75b6584a8996cd51103cbd30b3610a5e6))
* **instrument:** implement command to create
  record ([7c2eb6d](https://github.com/eduardoleolim/organizador-pec-6-60/commit/7c2eb6d9478781f8f0f6f492160be7a295b7aed9))
* **instrument:** implement command to update
  record ([26f6d14](https://github.com/eduardoleolim/organizador-pec-6-60/commit/26f6d147db3515eb4c75bece6a19b4356e7b0aeb))
* **instrument:** implement form to save new
  records ([e64970e](https://github.com/eduardoleolim/organizador-pec-6-60/commit/e64970ea9242b257b030311216d2d6e497a75837))
* **instrument:** implement header
  screen ([92270b2](https://github.com/eduardoleolim/organizador-pec-6-60/commit/92270b2a54022f6e28cb36a1429b6a8723c6d838))
* **instrument:** implement query for searching of
  records ([db73414](https://github.com/eduardoleolim/organizador-pec-6-60/commit/db7341484493f81e5aab622f4a779e24895c7ed2))
* **instrument:** implement
  repository ([89bc961](https://github.com/eduardoleolim/organizador-pec-6-60/commit/89bc961cc14351466ef7d9a7fdc22782f2a0172b))
* **instrument:** implement screen to show
  details ([3a5b73b](https://github.com/eduardoleolim/organizador-pec-6-60/commit/3a5b73b23b55da6513c792cfd9cd97d260ef5953))
* **instrument:** implement table for
  searching ([5ce715b](https://github.com/eduardoleolim/organizador-pec-6-60/commit/5ce715b2fc5bcf4624a3e6bc67f7441cc5582017))
* **instrument:** inject temp directory in
  screens ([6a6c8c8](https://github.com/eduardoleolim/organizador-pec-6-60/commit/6a6c8c8d00c94280ef5e42319b532b68e069c869))
* **instrument:** load column names from
  resources ([9fb7412](https://github.com/eduardoleolim/organizador-pec-6-60/commit/9fb741216ce2c1747ad527bd75990d21bc664350))
* **instrument:** search instrument by
  id ([337f046](https://github.com/eduardoleolim/organizador-pec-6-60/commit/337f0460e0c6977f2b03c5831c5bbe3544b52a6c))
* **instrument:** update instrument file in
  updater ([e4f1c7b](https://github.com/eduardoleolim/organizador-pec-6-60/commit/e4f1c7be3d2214230bf105c4505102998b2a7b2a))
* **instrument:** update
  record ([8ae5d5e](https://github.com/eduardoleolim/organizador-pec-6-60/commit/8ae5d5ee18882b9ebd4b85b2427e8e1868a43766))
* **instrument:** update record of
  instrument ([3592fa5](https://github.com/eduardoleolim/organizador-pec-6-60/commit/3592fa560da36ca0fe1b83c374510b295cd28c8e))
* **instrument:** update status in
  SIRESO ([68f2c88](https://github.com/eduardoleolim/organizador-pec-6-60/commit/68f2c88846a6cf68cc744445144210feda8e2aaa))
* **ktorm:** define models for instrument type and statistict
  type ([123770f](https://github.com/eduardoleolim/organizador-pec-6-60/commit/123770f44bc86146eb41f7139f28d1f4f6a8f4a7))
* load catalogs after database is
  created ([0ff020d](https://github.com/eduardoleolim/organizador-pec-6-60/commit/0ff020d4558c656cc38cd8895b10e5a9936c82ac))
* **main-window:** add icon
  option ([7c23b78](https://github.com/eduardoleolim/organizador-pec-6-60/commit/7c23b78e9876a271a45d5b3007d2f184f02cc29c))
* **main-window:** change window
  implementation ([4a89657](https://github.com/eduardoleolim/organizador-pec-6-60/commit/4a896572a7fbf576af9e373a11e0d8b7fe3fd101))
* **main-window:** config maximized
  bounds ([1e6232b](https://github.com/eduardoleolim/organizador-pec-6-60/commit/1e6232b4ec8ef1c80e3659a19fa0aeea2740721a))
* **main-window:** set dark and light color
  schema ([2bc4844](https://github.com/eduardoleolim/organizador-pec-6-60/commit/2bc484470c7b8b30e00f7e5a43d4d7774284597d))
* make titleBar button
  transparent ([d9d828d](https://github.com/eduardoleolim/organizador-pec-6-60/commit/d9d828dbdf5a28ff6aa9faf5c3e1b719916eef6e))
* **municipality:** add handlers to
  bus ([ff489fd](https://github.com/eduardoleolim/organizador-pec-6-60/commit/ff489fde9929b3ef77b59fdc9590c08c5c3879b6))
* **municipality:** create
  record ([6a32894](https://github.com/eduardoleolim/organizador-pec-6-60/commit/6a32894dcb9faa9f98b55bb0e563a2542df83af2))
* **municipality:** create with
  command ([120dfe3](https://github.com/eduardoleolim/organizador-pec-6-60/commit/120dfe3abda8d717ccf92acaf68750efa8a31891))
* **municipality:** delete record with
  command ([878d439](https://github.com/eduardoleolim/organizador-pec-6-60/commit/878d439f8e17b08da70d655235cd59fe912c1c37))
* **municipality:** implement base
  view ([70cb7ae](https://github.com/eduardoleolim/organizador-pec-6-60/commit/70cb7ae352f13eaf8ab89b2b91dcf01136ba3364))
* **municipality:** implement GUI for
  management ([e3eeac8](https://github.com/eduardoleolim/organizador-pec-6-60/commit/e3eeac81ed48b5c787558bf6bc9d89c9b6050f38))
* **municipality:** implement repository with
  ktorm ([a7e190c](https://github.com/eduardoleolim/organizador-pec-6-60/commit/a7e190c17f391af15d2090b512bad20384c7d39b))
* **municipality:** implement states for crud
  operations ([5e08212](https://github.com/eduardoleolim/organizador-pec-6-60/commit/5e08212850fef597718cc1aa2c5bc0c4ebf1065e))
* **municipality:** implement states for
  deleting ([53de546](https://github.com/eduardoleolim/organizador-pec-6-60/commit/53de5462c07aee63ddb2eb7d93f4e49c59a4ab68))
* **municipality:** load column names of table from
  resource ([5a2a7aa](https://github.com/eduardoleolim/organizador-pec-6-60/commit/5a2a7aabecdd422f62462722f4136211360464fc))
* **municipality:** load query handler in
  bus ([655bd77](https://github.com/eduardoleolim/organizador-pec-6-60/commit/655bd7729ff00c945859f4ae963973220498a2be))
* **municipality:** load text values from
  resource ([6b0c65f](https://github.com/eduardoleolim/organizador-pec-6-60/commit/6b0c65f632669d02f9363550b06dee1cfcd8247f))
* **municipality:** order municipality searching by
  keycode ([73ca5c5](https://github.com/eduardoleolim/organizador-pec-6-60/commit/73ca5c5ca8a22b387d41effb7352d80588965868))
* **municipality:** search by federal-entity's
  id ([2c8a1dd](https://github.com/eduardoleolim/organizador-pec-6-60/commit/2c8a1dd36d37058e73db6eba727e8bd628539c83))
* **municipality:** search by
  id ([0360749](https://github.com/eduardoleolim/organizador-pec-6-60/commit/03607492033bc0c5898e97ae3ca2b82cff7124f6))
* **municipality:** search by
  term ([af7fcd8](https://github.com/eduardoleolim/organizador-pec-6-60/commit/af7fcd8359766c2b1ffed6f2b20c677a0ca31611))
* **municipality:** select municipality with
  keyboard ([9400ae7](https://github.com/eduardoleolim/organizador-pec-6-60/commit/9400ae7804f9ec19fff787225ec77b0c84b938e5))
* **municipality:** update
  record ([8194507](https://github.com/eduardoleolim/organizador-pec-6-60/commit/8194507e3c08f07d0b3ef5906699a4582fc89bd4))
* **municipality:** update
  record ([6389e82](https://github.com/eduardoleolim/organizador-pec-6-60/commit/6389e82ff9b02108891cb8ef24496fa299357d55))
* navigate with screen
  provider ([69d259a](https://github.com/eduardoleolim/organizador-pec-6-60/commit/69d259acaf1b004d9d9f4c3d4a55336de7ed29f9))
* **pdf-viewer:** add padding to zoom
  slider ([c167a74](https://github.com/eduardoleolim/organizador-pec-6-60/commit/c167a74cf2888f95f1f0c52c4abf432e541d0ab0))
* **pdf-viewer:** add scrollbars and encapsulate page
  composable ([e89dca9](https://github.com/eduardoleolim/organizador-pec-6-60/commit/e89dca991e78d3f532bc63d136cca9ac04782c73))
* **pdf-viewer:** close document if pdfPath is
  null ([0b48fe7](https://github.com/eduardoleolim/organizador-pec-6-60/commit/0b48fe7a7c197503f3065665be92484f2dab9c39))
* **pdf-viewer:** customize container
  color ([19b2bab](https://github.com/eduardoleolim/organizador-pec-6-60/commit/19b2bab6dcc698a7680989a2ecdc1e2e923fb4e7))
* **pdf-viewer:** internationalize text
  labels ([5e33420](https://github.com/eduardoleolim/organizador-pec-6-60/commit/5e334201d40070de4b7208c1a5e2621e2cb057a2))
* **pdf-viewer:** load pages asynchronously and navigate with
  pager ([9f88690](https://github.com/eduardoleolim/organizador-pec-6-60/commit/9f88690a88a3fbf4d0d964dbf592a0b7eaf31c62))
* **pdf-viewer:** support horizontal
  scroll ([d015d51](https://github.com/eduardoleolim/organizador-pec-6-60/commit/d015d511a6b83cb1e973a674bd18ac92a94075fb))
* request database password if it doesn't
  exist ([7190a71](https://github.com/eduardoleolim/organizador-pec-6-60/commit/7190a7157547f4eaf425f39a5bd55e62389ad267))
* **role:** define roles for kind of
  user ([64500f0](https://github.com/eduardoleolim/organizador-pec-6-60/commit/64500f0b22017e6586c4bb85bf99e933b78be6da))
* **router:** add catalog views to
  router ([2a35f0f](https://github.com/eduardoleolim/organizador-pec-6-60/commit/2a35f0f71e8f4cdae7c7b542cb5963c50f5b4ccd))
* **router:** initialize router with auth
  screen ([97bc6c0](https://github.com/eduardoleolim/organizador-pec-6-60/commit/97bc6c0b85c4de0937adc3dfd3f5e9380aade36b))
* share tray state for
  notifications ([4d10495](https://github.com/eduardoleolim/organizador-pec-6-60/commit/4d10495967fe22f60602333c65f1ccd95a64bbb5))
* show tooltip in action buttons of
  tables ([f8315d6](https://github.com/eduardoleolim/organizador-pec-6-60/commit/f8315d64b1861b057c4116371c6264a0fbda65a2))
* show version in auth
  screen ([0400f92](https://github.com/eduardoleolim/organizador-pec-6-60/commit/0400f92c7d7260ff51037d6a8d3e1d1b38fe2b3e))
* **statistic-type:** check if record is used by agency before delete
  it ([562b469](https://github.com/eduardoleolim/organizador-pec-6-60/commit/562b469e6ad9861bdd643fdb8e6fc025e8dda7e8))
* **statistic-type:** create and update with
  form ([4cf8a0b](https://github.com/eduardoleolim/organizador-pec-6-60/commit/4cf8a0b3d260f52a9e830c595cb9146d38c95863))
* **statistic-type:** create
  record ([1441770](https://github.com/eduardoleolim/organizador-pec-6-60/commit/14417706d73620a3c67c89d74a34547e91d8f87b))
* **statistic-type:** create record with
  command ([3c3636b](https://github.com/eduardoleolim/organizador-pec-6-60/commit/3c3636b64df97819b4990275e5bbc5d686553492))
* **statistic-type:** delete statistic
  type ([39d6139](https://github.com/eduardoleolim/organizador-pec-6-60/commit/39d61392ab6adb223de48e1fdde53973740f45b8))
* **statistic-type:** implement base
  view ([67c92b0](https://github.com/eduardoleolim/organizador-pec-6-60/commit/67c92b03b128abdc2fe6233bcd4b32ab5040798f))
* **statistic-type:** implement dialog for
  deleting ([109a10b](https://github.com/eduardoleolim/organizador-pec-6-60/commit/109a10b802046f1d892f9312b9b9fb84c54e96c8))
* **statistic-type:** implement matching and count
  methods ([c6720a3](https://github.com/eduardoleolim/organizador-pec-6-60/commit/c6720a32f6175cc9a0a67f271a3d289a3f051677))
* **statistic-type:** implement table for searching and
  model ([8b5dbc5](https://github.com/eduardoleolim/organizador-pec-6-60/commit/8b5dbc52484c7b50aecef0cd6cf997c2c1c22dd7))
* **statistic-type:** load command handlers in command
  bus ([30bf503](https://github.com/eduardoleolim/organizador-pec-6-60/commit/30bf503d36a6286fcb4e984ec31f36c08b70e612))
* **statistic-type:** load query handlers in query
  bus ([be85dcc](https://github.com/eduardoleolim/organizador-pec-6-60/commit/be85dccacd2e4a4cb9163e951bbfab2721f7ca28))
* **statistic-type:** load text values in
  form ([a33b101](https://github.com/eduardoleolim/organizador-pec-6-60/commit/a33b1018b617ecb3c9fa8c70ff6c1b1ac0f7067a))
* **statistic-type:** search statistic type by
  id ([6e942ba](https://github.com/eduardoleolim/organizador-pec-6-60/commit/6e942ba336a7b813e4ac377598d10c9501ee2d55))
* **statistic-type:** search statistic
  types ([3aa22b7](https://github.com/eduardoleolim/organizador-pec-6-60/commit/3aa22b7c85e7a9602ea2f391298475219197c06c))
* **statistic-type:** search statistic types by
  term ([7035760](https://github.com/eduardoleolim/organizador-pec-6-60/commit/7035760cd334f8bb804099be40b1e632788a95a0))
* **statistic-type:** update
  record ([61a462c](https://github.com/eduardoleolim/organizador-pec-6-60/commit/61a462ca4f600aee65ba28f6dd62a089a1df9842))
* **statistic-type:** update with
  command ([d45ddc0](https://github.com/eduardoleolim/organizador-pec-6-60/commit/d45ddc0e73d0fd83ab1245c7ecba884067a1d0e0))
* **statistic-type:** validate instrument
  types ([9d2b2ad](https://github.com/eduardoleolim/organizador-pec-6-60/commit/9d2b2ad0a379f3fdb0e3600b913697b0e3e53e58))
* support cqrs
  pattern ([d673f1d](https://github.com/eduardoleolim/organizador-pec-6-60/commit/d673f1deff2cb8b1e979b1aa9de61e5de1bf1bd6))
* support criteria
  pattern ([84f2b21](https://github.com/eduardoleolim/organizador-pec-6-60/commit/84f2b213a7815d0de6218f75f70e3717f8dfd49b))
* support for instruments
  argument ([8700436](https://github.com/eduardoleolim/organizador-pec-6-60/commit/8700436f5552237d50184c82e39b27a0a41d9933))
* **table:** add header option for
  composable ([e73c467](https://github.com/eduardoleolim/organizador-pec-6-60/commit/e73c467858f165e5b3d473764e3d68c8f6e1178c))
* **table:** debounce
  searching ([c0db71a](https://github.com/eduardoleolim/organizador-pec-6-60/commit/c0db71ac58463cb6c919e60b37f3b9f76e583ccc))
* **table:** default pointer on trailing
  icon ([e98c817](https://github.com/eduardoleolim/organizador-pec-6-60/commit/e98c8175110c51256bd85bcfa688bf9a8c7d56de))
* **table:** reset page index before
  searching ([0277929](https://github.com/eduardoleolim/organizador-pec-6-60/commit/02779292913a74d72401de1b0fcd7f1e9c4311f2))
* **theme:** implement montserrat
  typography ([f562b14](https://github.com/eduardoleolim/organizador-pec-6-60/commit/f562b14967fe8626aefefa3765a23faca7139720))
* **titlebar:** add titlebar to
  window ([e0b7d25](https://github.com/eduardoleolim/organizador-pec-6-60/commit/e0b7d25c5dda4292489ac6aeecc44583e68cac75))
* **titlebar:** disable maximize button when isResizable is
  false ([713725d](https://github.com/eduardoleolim/organizador-pec-6-60/commit/713725d214816302e80e0e5a90e3031844768095))
* update home config in each
  view ([2fa996e](https://github.com/eduardoleolim/organizador-pec-6-60/commit/2fa996e5beffca6c4d6f098c9f337d9cacbf9a42))
* use borderless
  window ([f503d84](https://github.com/eduardoleolim/organizador-pec-6-60/commit/f503d84868e8d16f3cd2061764bd5e384b49ff4c))
* use OpenGL in
  skiko ([a7436d0](https://github.com/eduardoleolim/organizador-pec-6-60/commit/a7436d01de6a74d30bee92ac3f68f2066120df5b))
* use values of xml in auth
  screen ([8935920](https://github.com/eduardoleolim/organizador-pec-6-60/commit/8935920c802b641be5453ad3a4a29c73438c038d))
* **user:** define domain
  layer ([6c5da0b](https://github.com/eduardoleolim/organizador-pec-6-60/commit/6c5da0b5f7f4bfdbcfc5137a5bceefd21dd71f76))
* **user:** implement user repository with
  ktorm ([7609584](https://github.com/eduardoleolim/organizador-pec-6-60/commit/76095843551679e280977e6d7ccf7f142fd0b32d))
* **user:** search users by
  criteria ([c4b2c0a](https://github.com/eduardoleolim/organizador-pec-6-60/commit/c4b2c0a20b47b46bda76f41e3840ab7ee44f6c39))
* **window:** add border for linux
  distribution ([5c36468](https://github.com/eduardoleolim/organizador-pec-6-60/commit/5c364680580afff39f1fe31617f72cb4d63f1c40))
* **window:** add callback for closing request in title
  bar ([99e1257](https://github.com/eduardoleolim/organizador-pec-6-60/commit/99e1257d91b87a6a0070510c454fee6ea6f8caf7))
* **window:** add parameter to define minimum height of
  titlebar ([18252cb](https://github.com/eduardoleolim/organizador-pec-6-60/commit/18252cb2880f0237699c4fd9c03497b38092bff3))
* **window:** config compose
  window ([ff13d32](https://github.com/eduardoleolim/organizador-pec-6-60/commit/ff13d32e9fdf42ece52ad00b642af7cd2a9011df))
* **window:** make icon
  clickable ([77b2c89](https://github.com/eduardoleolim/organizador-pec-6-60/commit/77b2c89478a60075fd6862bfb34e6a1a9ed482da))
* **window:** provide ComposeWindow instance with
  LocalWindow ([3540688](https://github.com/eduardoleolim/organizador-pec-6-60/commit/35406880cab9685f508210a2ee003f43ec74dbba))
* **window:** show default
  icon ([013853b](https://github.com/eduardoleolim/organizador-pec-6-60/commit/013853b33c3051942e2880becc9de6175204e9ac))
* **window:** switch theme among light, dark and default
  theme ([91e1985](https://github.com/eduardoleolim/organizador-pec-6-60/commit/91e1985ed1446ee6081936631dd61cf0d0656f06))

### Bug Fixes

* add import of
  QueryBus ([7ac8393](https://github.com/eduardoleolim/organizador-pec-6-60/commit/7ac83935f8123e629bd5bb979c3fd18940b1f5a7))
* **agency:** clear caches before return
  response ([8a3baba](https://github.com/eduardoleolim/organizador-pec-6-60/commit/8a3baba8bccf9b178e942bf3744d19ade2edfab2))
* **agency:** set column value of statistic
  type ([2aa9280](https://github.com/eduardoleolim/organizador-pec-6-60/commit/2aa9280852beb84f232bd4a6f75f0982166b0a1c))
* **agency:** use Agency field for default order of
  searching ([8406e8f](https://github.com/eduardoleolim/organizador-pec-6-60/commit/8406e8f0d005d47a2d4cad2fc4ea130a930aa8a8))
* **agency:** use repository for
  validation ([92eb79a](https://github.com/eduardoleolim/organizador-pec-6-60/commit/92eb79af84dee7fc5dd1c450b270abc175fcdf81))
* **app:** downgrade voyager to switch material theme
  correctly ([328e263](https://github.com/eduardoleolim/organizador-pec-6-60/commit/328e263c462276c3ca30332212567da0c85cb597))
* **app:** set value
  skiko.renderApi ([7694ab0](https://github.com/eduardoleolim/organizador-pec-6-60/commit/7694ab054f8ed027b6be8526e80f124b26be5e2f))
* **auth:** restore window size when
  logout ([461aee8](https://github.com/eduardoleolim/organizador-pec-6-60/commit/461aee854bc4c8a4a38addc32936211cea6b7665))
* **command-handler:** rollback transaction if either is
  left ([ef146c7](https://github.com/eduardoleolim/organizador-pec-6-60/commit/ef146c7cfcdf9ccc0adc701d71edb9c3c3b9a4a3))
* **composable:** disable trailingIcon of
  select ([7a930e9](https://github.com/eduardoleolim/organizador-pec-6-60/commit/7a930e923692a64e0fce586b0a8fc7beb9c1ecd8))
* **composable:** focus next element to
  select ([6cf1de8](https://github.com/eduardoleolim/organizador-pec-6-60/commit/6cf1de81f44d2424ae11c93e28d77e83f5133801))
* **criteria:** fix initialization of
  OrderType ([03b6239](https://github.com/eduardoleolim/organizador-pec-6-60/commit/03b6239724a3632f79890eb3df6a0eb7d3150725))
* **federal-entity:** add or conditions to
  query ([5bd851b](https://github.com/eduardoleolim/organizador-pec-6-60/commit/5bd851b426fc36d82e2dcf9cfe1ec88f9d24bbaf))
* **federal-entity:** update form state on update or create
  failure ([ad1c825](https://github.com/eduardoleolim/organizador-pec-6-60/commit/ad1c82545f8dcdbaf1059641cc86666d6fc91af6))
* **home:** assing property of HomeConfig
  correctly ([1143111](https://github.com/eduardoleolim/organizador-pec-6-60/commit/11431116e96d0a7b34da3f7edbbb6195ba94b896))
* initialize agency searcher in app
  module ([56ba5d3](https://github.com/eduardoleolim/organizador-pec-6-60/commit/56ba5d3d81507f4b70dc0f58d58c2a17f928951a))
* **instrument-type:** show correct title of
  form ([88ea56d](https://github.com/eduardoleolim/organizador-pec-6-60/commit/88ea56dc15949147b7aa2c67ef864440795abc19))
* **instrument-type:** use count() in criteria
  parser ([2d26ff8](https://github.com/eduardoleolim/organizador-pec-6-60/commit/2d26ff8fa957734409480846a0410d146ddc8c08))
* **instrument:** assign commands handler
  correctly ([e948f9b](https://github.com/eduardoleolim/organizador-pec-6-60/commit/e948f9b92962dcadc023472c4217c2203a1433a9))
* **instrument:** delete instrument before instrument
  file ([ff955ea](https://github.com/eduardoleolim/organizador-pec-6-60/commit/ff955ea10f55a801d1f70de9c49e864eeb59244d))
* **instrument:** set correctly federal entity id filter in
  criteria ([dedb01c](https://github.com/eduardoleolim/organizador-pec-6-60/commit/dedb01c49ddb827a36f09ee8f9472190d6998c25))
* **instrument:** set month
  parameter ([d952fd0](https://github.com/eduardoleolim/organizador-pec-6-60/commit/d952fd06ad9215815ed77b77831f5a206ddabe6b))
* **instrument:** validate month between 1 and
  12 ([1fe4714](https://github.com/eduardoleolim/organizador-pec-6-60/commit/1fe4714349cae67d0227269df165b2d5b114316e))
* **instrument:** validate month between 1 and 12
  correctly ([6a44eca](https://github.com/eduardoleolim/organizador-pec-6-60/commit/6a44eca2de8ddca53abc23cd1f57e1d10c489b06))
* **municipality:** avoid loop in federal entities
  searching ([a9502ac](https://github.com/eduardoleolim/organizador-pec-6-60/commit/a9502aca01e1773ed775af9ef3f8bc24eb06369f))
* **municipality:** check if municipality exists by keyCode and federal
  entity ([ca17cbb](https://github.com/eduardoleolim/organizador-pec-6-60/commit/ca17cbb0e893b4eafa26a5d4d05d825f365defd9))
* **municipality:** select from municipality table in
  count ([95b1eab](https://github.com/eduardoleolim/organizador-pec-6-60/commit/95b1eab100916d98778a5dae6043aab0d18b160a))
* **municipality:** set keyCode when save
  record ([c5a0b0a](https://github.com/eduardoleolim/organizador-pec-6-60/commit/c5a0b0aac96074d559fb88808b352a3f7597d241))
* **municipality:** set parameters correctly in
  updater ([6c6330a](https://github.com/eduardoleolim/organizador-pec-6-60/commit/6c6330abd026a37e2cdd8512cb24f9f02c2e74c0))
* **municipality:** validate
  correctly ([4ae150d](https://github.com/eduardoleolim/organizador-pec-6-60/commit/4ae150dce95ad81f855a6079110db5f3629e3ff7))
* **municipality:** validate duplicates correctly by federal entity
  id ([efb36cb](https://github.com/eduardoleolim/organizador-pec-6-60/commit/efb36cb431dc51bc05a71594f37624b510bd5cdd))
* **pdf-viewer:** scroll page vertically on one page
  mode ([35e0fce](https://github.com/eduardoleolim/organizador-pec-6-60/commit/35e0fcec5cf9529ec90ae3abd312a3c03b3f36a5))
* **role:** make private constructor's
  parameters ([554a0a8](https://github.com/eduardoleolim/organizador-pec-6-60/commit/554a0a82c46f03dfa94815a681c46947da276e29))
* round clickable titlebar
  item ([8e541d8](https://github.com/eduardoleolim/organizador-pec-6-60/commit/8e541d84e50de6006a007010e5817352472d0be0))
* save database password
  correctly ([6b59c75](https://github.com/eduardoleolim/organizador-pec-6-60/commit/6b59c758bd74f90a5a6f9eef8cd6fa35921848c0))
* **statistic-type:** inject instrument-type
  repository ([0b85a1f](https://github.com/eduardoleolim/organizador-pec-6-60/commit/0b85a1f5348ed511bb1a28249c34234fa1f0a2b0))
* **statistic-type:** set limit and offset
  correctly ([213ba5d](https://github.com/eduardoleolim/organizador-pec-6-60/commit/213ba5d5b77671604ff37a34c416db5f0b483f90))
* **temp:** delete temp directory before app
  closes ([eaf9e4c](https://github.com/eduardoleolim/organizador-pec-6-60/commit/eaf9e4cfa8c96d32e19eb08a55514535d60fd3b1))
* **titlebar:** set close button's color as
  red ([0facbca](https://github.com/eduardoleolim/organizador-pec-6-60/commit/0facbca0d8800b5478a4b732885155913c34bfdc))

### Reverts

* re-set JAVA_HOME to
  PATH ([0e24432](https://github.com/eduardoleolim/organizador-pec-6-60/commit/0e2443240df7de9d007331d24e8e749be5c957a2))
