resource "azurerm_container_registry" "container_registry" {
  name                = "griersonFlixtubeContainerRegisty"
  resource_group_name = azurerm_resource_group.flixtube.name
  location            = var.location
  admin_enabled       = true
  sku                 = "Basic"
}
