# Integración M1AU en GigMap-mobile

## Visión general
Este módulo conecta la app GigMap con el microservicio M1AU (chatbot). Incluye el cliente de red Retrofit, DTOs, servicio, repositorio, ViewModel y componentes de UI para conversar con M1AU y mostrar la lista de conciertos sugeridos.

## Archivos creados / actualizados

### Networking
- **services/m1au/M1AUApiClient.kt**: Configura Retrofit (baseUrl `http://10.0.2.2:3030/`, interceptor de logging) para llamar al microservicio.
- **services/m1au/M1AUService.kt**: Interface Retrofit con el endpoint `POST /chat`.
- **services/m1au/dto/M1AUChatRequestDto.kt**: DTO de solicitud (`message`, `userId`).
- **services/m1au/dto/M1AUChatResponseDto.kt**: DTO de respuesta con `message`, `concerts`, `filters`, `meta` y los datos anidados de artista/venue/plataforma.

### Dominio / Modelos
- **model/m1au/M1AUBotReply.kt**: Modelos que usa la UI (`M1AUBotReply` y `M1AUConcertCard`).
- **services/m1au/M1AUChatService.kt**: Mapea DTO → modelo y maneja la llamada `sendMessage`.
- **services/m1au/M1AUChatRepository.kt**: Expone `askM1AU` para ViewModels.

### ViewModel
- **viewmodel/M1AUChatViewModel.kt**:
  - Gestiona estado del chat (`messages`, `concerts`, `isLoading`, `error`).
  - `sendMessage` agrega burbuja del usuario, llama al repo y añade respuesta del bot.

### UI Compose
- **view/chat/M1AUChatScreen.kt**:
  - `M1AUChatModal`: Dialogo modal con historial, input y cards de conciertos.
  - `ChatBubble`, `ConcertCard`: componentes reutilizables con Coil (`AsyncImage`).

### Integración en Home
- **view/HomeContent.kt** (actualizado):
  - Añade `chatViewModel` y estado `isChatOpen`.
  - Nuevo FAB con icono de chat para abrir el modal.
  - Usa `M1AUChatModal` y navega a perfiles con `artistId`.

## Notas de uso
1. El microservicio debe correr en `http://10.0.2.2:3030/` para pruebas en emulador Android.
2. `M1AUChatViewModel` puede recibir `userId` si se desea personalizar consultas.
3. `ConcertCard` renderiza los campos que M1AU entrega (`imageUrl`, `formattedDate`, artista, venue, plataforma).
4. Para abrir el chat desde otros composables, reutiliza `M1AUChatModal` y comparte el `ViewModel` si quieres conservar historial global.
