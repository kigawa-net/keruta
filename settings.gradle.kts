rootProject.name = "keruta"

// Core modules
include("core:domain")
include("core:usecase")

// Infrastructure modules
include("infra:persistence")
include("infra:security")
include("infra:app")

// API modules
include("api")
