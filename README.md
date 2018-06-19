# Simple REST Service

To build an application run:
```bash
maven build
```


### GET /category/[id]

Example: GET http://example.com/category/cfc6247d-6a2d-4c0a-91ef-a12b70e32108

Response body:
```
{
  "id": "cfc6247d-6a2d-4c0a-91ef-a12b70e32108",
  "name": "Drawing",
  "slug": "drawing",
  "parentCategory": {
    "id": "c62ad8de-a71e-45ac-a329-98f9cc6e306f",
    "name": "Art",
    "slug": "art",
    "parentCategory": null,
    "childCategory": null,
    "isVisible": true
  },
  "childCategory": null,
  "isVisible": true
}
```


### GET /category/[slug]

Example: GET http://www.example.com/category/art

Response body:
```
{
  "id": "c62ad8de-a71e-45ac-a329-98f9cc6e306f",
  "name": "Art",
  "slug": "art",
  "parentCategory": null,
  "childCategory": [
    {
      "id": "cfc6247d-6a2d-4c0a-91ef-a12b70e32108",
      "name": "Drawing",
      "slug": "drawing",
      "parentCategory": {
        "id": "c62ad8de-a71e-45ac-a329-98f9cc6e306f",
        "name": "Art",
        "slug": "art",
        "parentCategory": null,
        "childCategory": null,
        "isVisible": true
      },
      "childCategory": null,
      "isVisible": true
    },
    {
      "id": "b0ef8371-f8fa-4fe4-81af-13f285f1c3b8",
      "name": "Test",
      "slug": "test",
      "parentCategory": {
        "id": "c62ad8de-a71e-45ac-a329-98f9cc6e306f",
        "name": "Art",
        "slug": "art",
        "parentCategory": null,
        "childCategory": null,
        "isVisible": true
      },
      "childCategory": null,
      "isVisible": true
    }
  ],
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

400 Bad Request is returned in case of error.


### PATCH /category/edit

Example: PATCH http://www.example.com/category/edit

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
    "childCategory": null,
    "isVisible": true
}
```