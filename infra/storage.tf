resource "azurerm_storage_account" "storage" {
  name                     = "${var.name}storage"
  resource_group_name      = azurerm_resource_group.flixtube.name
  location                 = azurerm_resource_group.flixtube.location
  account_tier             = "Standard"
  account_replication_type = "LRS"
}

resource "azurerm_storage_container" "videos" {
  name                  = "videos"
  storage_account_name  = azurerm_storage_account.storage.name
  container_access_type = "private"
}
