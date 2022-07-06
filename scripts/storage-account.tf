resource "azurerm_storage_account" "flixtube" {
  name                     = "stgriersonflixtubedev001"
  resource_group_name      = azurerm_resource_group.flixtube.name
  location                 = azurerm_resource_group.flixtube.location
  account_tier             = "Standard"
  account_replication_type = "LRS"
  account_kind             = "BlobStorage"
}

resource "azurerm_storage_container" "flixtube" {
  name                  = "videos"
  storage_account_name  = azurerm_storage_account.flixtube.name
  container_access_type = "private"
}
