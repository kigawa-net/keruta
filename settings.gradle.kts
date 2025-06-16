rootProject.name = "keruta"

// Core modules
include("core:domain")
include("core:usecase")

// Infrastructure modules
include("infra:persistence")
include("infra:security")

// API modules
include("api:task")
include("api:document")
include("api:repository")