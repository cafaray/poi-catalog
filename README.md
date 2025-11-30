# POI Catalog API

Quarkus + Kotlin implementation of the POI Catalog API with Firebase Firestore backend.

## Architecture

**Tech Stack:**
- Quarkus 3.6.4 - Reactive Java framework
- Kotlin 1.9.22 - Programming language
- Firebase Firestore - NoSQL database
- RESTEasy Reactive - REST endpoints
- Jackson - JSON serialization

**Design Pattern:** Layered architecture
- `resource` - REST endpoints (controllers)
- `service` - Business logic
- `repository` - Data access layer (Firestore)
- `model` - Data models and entities
- `filter` - Request filters (authentication)
- `config` - Application configuration

## Project Structure

```
src/main/kotlin/com/walkers/poi/
├── config/
│   └── FirebaseConfig.kt          # Firebase initialization
├── filter/
│   └── ApiKeyFilter.kt            # API key authentication
├── model/
│   ├── Models.kt                  # DTOs and enums
│   └── POIEntity.kt               # Firestore entity
├── repository/
│   ├── POIRepository.kt           # POI data access
│   └── MediaRepository.kt         # Media data access
├── resource/
│   ├── POIResource.kt             # POI endpoints
│   └── MediaResource.kt           # Media endpoints
└── service/
    ├── POIService.kt              # POI business logic
    └── MediaService.kt            # Media business logic
```

## Build & Run

**Prerequisites:**
- JDK 17+
- Gradle 8.5+

**Development mode:**
```bash
./gradlew quarkusDev
```

**Build:**
```bash
./gradlew build
```

**Run tests:**
```bash
./gradlew test
```

**Production build:**
```bash
./gradlew build -Dquarkus.package.type=uber-jar
java -jar build/poi-catalog-1.0.0-runner.jar
```

## Authentication

All endpoints require Firebase Authentication. Include the Firebase ID token in the Authorization header:

```
Authorization: Bearer <firebase-id-token>
```

**Obtaining a token:**

1. **Using Firebase REST API:**
```bash
# Sign in with email/password
curl -X POST 'https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=AIzaSyCQ...J4FwVpLwvK68' \
  -H 'Content-Type: application/json' \
  -d '{
    "email": "user@example.com",
    "password": "your-password",
    "returnSecureToken": true
  }'

# Response contains idToken field
```

2. **Using Firebase SDK (JavaScript):**
```javascript
import { getAuth, signInWithEmailAndPassword } from 'firebase/auth';

const auth = getAuth();
const userCredential = await signInWithEmailAndPassword(auth, email, password);
const token = await userCredential.user.getIdToken();
```

3. **Using Firebase SDK (Python):**
```python
import firebase_admin
from firebase_admin import auth

# Create custom token or verify existing token
token = auth.create_custom_token(uid)
```

## API Endpoints

### POI Management
- `POST /v1/pois` - Create POI
- `GET /v1/pois` - List POIs (paginated)
- `GET /v1/pois/{poiId}` - Get POI by ID
- `PUT /v1/pois/{poiId}` - Update POI
- `DELETE /v1/pois/{poiId}` - Delete POI
- `POST /v1/pois/bulk-import` - Bulk import POIs
- `GET /v1/pois/search` - Search POIs

### Media Management
- `POST /v1/pois/{poiId}/media` - Upload media
- `DELETE /v1/pois/{poiId}/media/{mediaId}` - Delete media

### Health
- `GET /q/health` - Health check

## Usage Examples

### Get Authentication Info
```bash
curl -X GET http://localhost:8080/v1/auth/info
```

**Response:**
```json
{
  "success": true,
  "data": {
    "authMethod": "Firebase Authentication",
    "tokenType": "Bearer",
    "headerFormat": "Authorization: Bearer <firebase-id-token>",
    "documentation": "https://firebase.google.com/docs/auth/admin/verify-id-tokens"
  },
  "timestamp": "2024-01-15T10:00:00Z"
}
```

### Login Instructions
```bash
curl -X POST http://localhost:8080/v1/auth/login
```

**Response:**
```json
{
  "success": true,
  "data": {
    "message": "Use Firebase Authentication SDK to obtain ID token",
    "instructions": "Send POST request to Firebase Auth REST API with email/password"
  },
  "timestamp": "2024-01-15T10:00:00Z"
}
```

### Firebase Login Example
```bash
# Step 1: Get Firebase ID token
curl -X POST 'https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=AIzaSyCQ...J4FwVpLwvK68' \
  -H 'Content-Type: application/json' \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "returnSecureToken": true
  }'

# Step 2: Extract idToken from response and use it in API calls
# Response: {"idToken": "eyJhbGc...", "email": "user@example.com", ...}
```

### Create POI
```bash
curl -X POST http://localhost:8080/v1/pois \
  -H "Authorization: Bearer <firebase-id-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Eiffel Tower",
    "latitude": 48.8584,
    "longitude": 2.2945,
    "type": "monument",
    "description": "Iconic iron tower in Paris",
    "address": "Champ de Mars, Paris",
    "tags": ["landmark", "historical"]
  }'
```

### List POIs
```bash
curl -X GET "http://localhost:8080/v1/pois?page=1&limit=20" \
  -H "Authorization: Bearer <firebase-id-token>"
```

### Get POI by ID
```bash
curl -X GET http://localhost:8080/v1/pois/poi_123 \
  -H "Authorization: Bearer <firebase-id-token>"
```

### Update POI
```bash
curl -X PUT http://localhost:8080/v1/pois/poi_123 \
  -H "Authorization: Bearer <firebase-id-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Updated Name",
    "description": "Updated description"
  }'
```

### Search POIs
```bash
curl -X GET "http://localhost:8080/v1/pois/search?q=tower&tags=landmark,historical" \
  -H "Authorization: Bearer <firebase-id-token>"
```

### Upload POIs from File
```bash
curl -X POST http://localhost:8080/v1/pois/upload-file \
  -H "Authorization: Bearer <firebase-id-token>" \
  -F "file=@first_load.json"
```

### Bulk Import
```bash
curl -X POST http://localhost:8080/v1/pois/bulk-import \
  -H "Authorization: Bearer <firebase-id-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "pois": [
      {
        "name": "POI 1",
        "latitude": 40.7128,
        "longitude": -74.0060,
        "type": "landmark"
      },
      {
        "name": "POI 2",
        "latitude": 51.5074,
        "longitude": -0.1278,
        "type": "museum"
      }
    ]
  }'
```

### Upload Media
```bash
curl -X POST http://localhost:8080/v1/pois/poi_123/media \
  -H "Authorization: Bearer <firebase-id-token>" \
  -F "file=@image.jpg" \
  -F "type=image" \
  -F "caption=Beautiful view"
```

### Delete POI
```bash
curl -X DELETE http://localhost:8080/v1/pois/poi_123 \
  -H "Authorization: Bearer <firebase-id-token>"
```

## Testing with Postman

Import the Postman collection: `POI-Catalog-API.postman_collection.json`

**Steps:**
1. Import collection into Postman
2. Run "Firebase Login" request to get authentication token (automatically saved)
3. Test other endpoints (token is automatically included)
4. Variables `poi_id` and `media_id` are auto-populated from responses

## Configuration

Firebase configuration in `src/main/resources/firebase-config.json`:
```json
{
  "projectId": "lab-utilities",
  "authDomain": "lab-utilities.firebaseapp.com",
  "storageBucket": "lab-utilities.firebasestorage.app"
}
```

Application properties in `src/main/resources/application.properties`:
```properties
quarkus.http.port=8080
quarkus.application.name=poi-catalog
```
