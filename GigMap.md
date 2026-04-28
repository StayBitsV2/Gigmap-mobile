#  GigMap Mobile

## Estructura 
gigmap_frontend_sprint1/
├── MainActivity.kt
├── app/ (vacío)
└── bounded/
    ├── concerts/
    │   ├── model/
    │   │   ├── Concerts.kt
    │   │   ├── ConcertCreateRequest.kt
    │   │   ├── Venue.kt
    │   │   └── VenueRequest.kt
    │   ├── viewmodel/
    │   │   ├── ConcertViewModel.kt
    │   │   └── VenueViewModel.kt
    │   └── view/
    │       ├── ConcertsList.kt
    │       ├── ConcertDetails.kt
    │       └── CreateConcert.kt
    │
    ├── communities/
    │   ├── model/
    │   │   ├── Community.kt
    │   │   ├── Post.kt
    │   │   └── PostCreateRequest.kt
    │   ├── viewmodel/
    │   │   ├── CommunityViewModel.kt
    │   │   └── PostViewModel.kt
    │   └── view/
    │       ├── CommunitiesList.kt
    │       ├── Community.kt
    │       └── CreatePost.kt
    │
    ├── relatedevents/
    │   └── model/
    │       └── RelatedEvent.kt
    │
    ├── users/
    │   ├── model/
    │   │   ├── Users.kt
    │   │   ├── LoginRequest.kt
    │   │   ├── LoginResponse.kt
    │   │   ├── RegisterRequest.kt
    │   │   └── CreateDeviceTokenRequest.kt
    │   ├── viewmodel/
    │   │   └── UserViewModel.kt
    │   └── view/
    │       ├── Login.kt
    │       ├── SignIn.kt
    │       ├── Profile.kt
    │       └── Welcome.kt
    │
    ├── public/
    │   └── view/
    │       ├── Home.kt
    │       ├── HomeContent.kt
    │       ├── Map.kt
    │       └── nav/
    │           └── Navi.kt
    │
    └── shared/
        ├── components/
        │   ├── BottomBar.kt
        │   ├── TopBar.kt
        │   └── RedGuideLayout.kt
        ├── services/
        │   ├── CloudinaryService.kt
        │   ├── FirebaseService.kt
        │   └── GoogleMapsService.kt
        ├── client/
        │   ├── RetrofitClient.kt
        │   └── WebService.kt
        ├── ui/
        │   └── theme/
        │       ├── Color.kt
        │       ├── Theme.kt
        │       └── Type.kt
        ├── model/
        │   ├── Platform.kt
        │   └── PlatformRequest.kt
        └── viewmodel/
            └── PlatformViewModel.kt
```

## Bounded Definidos

### 1. **Concerts**
- **Responsabilidad**: Gestión de conciertos y venues
- **Incluye**: Modelos de conciertos, venues, creación de conciertos, listado y detalles
- **Razón**: Venue es parte integral del dominio de conciertos

### 2. **Communities**
- **Responsabilidad**: Gestión de comunidades y posts
- **Incluye**: Modelos de comunidades, posts, creación y visualización
- **Razón**: Los posts pertenecen a comunidades, forman un dominio cohesivo

### 3. **RelatedEvents**
- **Responsabilidad**: Eventos relacionados
- **Incluye**: Modelo de eventos relacionados
- **Razón**: Dominio separado para futuras expansiones

### 4. **Users**
- **Responsabilidad**: Autenticación y gestión de usuarios
- **Incluye**: Login, registro, perfil, tokens de dispositivo
- **Razón**: Dominio de identidad y acceso

### 5. **Public**
- **Responsabilidad**: Vistas públicas y navegación principal
- **Incluye**: Home, Map, navegación principal
- **Razón**: Componentes de nivel superior que orquestan múltiples bounded contexts

### 6. **Shared**
- **Responsabilidad**: Código compartido entre todos los bounded contexts
- **Incluye**: Componentes UI, servicios, cliente HTTP, temas
- **Razón**: Infraestructura común reutilizable

