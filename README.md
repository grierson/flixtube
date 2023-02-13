# Flixtube

Bootstrapping microservices with docker and kubernetes and terraform in clj

## Setup

* <http://localhost:4001/video?path=bunny.mp4> - Video storage
* <http://localhost:4002/video?id=5d9e690ad76fe06a3d7ae416> - Video stream

Create Database: video-streaming
Create Collection: videos
Add record

{
    "_id" : ObjectId("5d9e690ad76fe06a3d7ae416"),
    "videoPath" : "bunny.mp4"
}
