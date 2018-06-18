# Simple REST Service

To build an application run:
```bash
maven build
```


### GET /category/[id]

Example: GET http://example.com/category/0d6c8a3a-74c8-47b3-ab5e-890da2e5e218

Response body:
```
{
    "id": "0d6c8a3a-74c8-47b3-ab5e-890da2e5e218",
    "name": "Music",
    "slug": "music",
    "parentCategory": null,
    "isVisible": true
}
```


### GET /category/[slug]

Example: GET http://www.example.com/category/music

Response body:
```
{
    "id": "0d6c8a3a-74c8-47b3-ab5e-890da2e5e218",
    "name": "Music",
    "slug": "music",
    "parentCategory": null,
    "isVisible": true
}
```


### POST /category/new

Example: POST http://www.example.com/category/new

Request:
```
{
    "id": "0d6c8a3a-74c8-47b3-ab5e-890da2e5e218",
    "name": "Music",
    "slug": "music",
    "parentCategory": null,
    "isVisible": true
}
```

Response body in case of success:
```
{
    "id": "0d6c8a3a-74c8-47b3-ab5e-890da2e5e218",
    "name": "Music",
    "slug": "music",
    "parentCategory": null,
    "isVisible": true
}
```

In case of error 400 Bad Request returned.


### PATCH /category/edit

Example: PATCH http://www.example.com/category/new

Request:
```
categoryId=0d6c8a3a-74c8-47b3-ab5e-890da2e5e218&isVisible=true
```

Response body in case of success:
```
{
    "id": "0d6c8a3a-74c8-47b3-ab5e-890da2e5e218",
    "name": "Music",
    "slug": "music",
    "parentCategory": null,
    "isVisible": true
}
```