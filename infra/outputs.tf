output "client_certificate" {
  value     = azurerm_kubernetes_cluster.cluster.kube_config[0].client_certificate
  sensitive = true
}

output "client_key" {
  value     = azurerm_kubernetes_cluster.cluster.kube_config[0].client_key
  sensitive = true
}

output "cluster_ca_certificate" {
  value     = azurerm_kubernetes_cluster.cluster.kube_config[0].cluster_ca_certificate
  sensitive = true
}

output "cluster_password" {
  value     = azurerm_kubernetes_cluster.cluster.kube_config[0].password
  sensitive = true
}

output "cluster_username" {
  value     = azurerm_kubernetes_cluster.cluster.kube_config[0].username
  sensitive = true
}

output "host" {
  value     = azurerm_kubernetes_cluster.cluster.kube_config[0].host
  sensitive = true
}

output "kube_config" {
  value     = azurerm_kubernetes_cluster.cluster.kube_config_raw
  sensitive = true
}

output "resource_group_name" {
  value = azurerm_resource_group.flixtube.name
}

output "registry_hostname" {
  value = azurerm_container_registry.container_registry.login_server
}

output "registry_un" {
  value = azurerm_container_registry.container_registry.admin_username
}

output "registry_pw" {
  value     = azurerm_container_registry.container_registry.admin_password
  sensitive = true
}

output "cluster_private_key" {
  value     = tls_private_key.key.private_key_pem
  sensitive = true
}

output "cluster_public_key" {
  value = tls_private_key.key.public_key_pem
}
