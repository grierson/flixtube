# Flixtube

Bootstrapping microservices with docker and kubernetes and terraform in clj

## Setup

- `cp .env.example .env`
- `cd infra`
- `az login`
- `terraform init`
- `terraform apply`
- make a cup of tea
- `terraform output --raw az_storage_connection_string`
- Copy result into `.env` as `AZURE_STORAGE_CONNECTION_STRING` value
- `cd ..` (Back to root)
- `make up` (Runs Docker compose)
- When done - `terraform destroy`

## Usage

- <http://localhost:4001/video?path=bunny.webm> - Video storage
- <http://localhost:4002/video?id=5d9e690ad76fe06a3d7ae416> - Video stream

## Explaination

- `infra` - Create Azure blob storage and uploads videos
- `mongo-seed` - Create Monogo collection and adds record for video path
- `Video storage` - Azure gateway to fetch video
- `Video stream` - Stream video in brower
- `History` - Consume Video stream events to log what videos
  have been watched and by who
