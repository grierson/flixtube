# Flixtube

Bootstrapping microservices with docker and kubernetes and terraform in clj

## Setup

* <http://localhost:4001/video?path=bunny.mp4> - Video storage
* <http://localhost:4002/video?id=5d9e690ad76fe06a3d7ae416> - Video stream

## Local

* `cd local-infra`
* `terraform apply`
* `terraform output --raw az_storage_connection_string` (Add to .env)
* `terraform destroy`

## Explaination

`local-infra` - Create Azure blob storage and uploads videos
`mongo-seed` - Create collection and adds record for video path

### Services

* Video storage - Access Azure to fetch file
* Video stream - Call Video storage service
* History - Log which videos are viewed 

## Infa

* local-infra - for running locally
