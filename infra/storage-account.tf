resource "azurerm_storage_account" "flixtubestorage" {
  name                     = "${var.name}0sa"
  resource_group_name      = azurerm_resource_group.flixtube.name
  location                 = var.location
  account_tier             = "Standard"
  account_replication_type = "LRS"
}

output "az_storage_connection_string" {
  value     = azurerm_storage_account.flixtubestorage.primary_connection_string
  sensitive = true
}

resource "azurerm_storage_container" "flixtubestoragecontainer" {
  name                  = "videos"
  storage_account_name  = azurerm_storage_account.flixtubestorage.name
  container_access_type = "blob"
}

resource "azurerm_storage_blob" "flixtubecontainerblob" {
  name                   = "bunny.webm"
  storage_account_name   = azurerm_storage_account.flixtubestorage.name
  storage_container_name = azurerm_storage_container.flixtubestoragecontainer.name
  type                   = "Block"
  source                 = "../video-storage/resources/bunny.webm"
}
