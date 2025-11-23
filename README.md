# EventByte - Documentation des API

## Table des matières
1. [API Événements](#api-événements)
2. [API Likes](#api-likes)
3. [API Tickets](#api-tickets)
4. [API Réservations](#api-réservations)
5. [Authentification](#authentification)

---

## API Événements

### 1. Récupérer les événements à découvrir
**Route:** `GET /events/discover`

**Query Parameters:**
```typescript
{
  lat: number,    // Latitude de l'utilisateur
  lng: number     // Longitude de l'utilisateur
}
```

**Réponse:** `200 OK`
```typescript
Array<{
  id: string,
  title: string,
  cover: string,              // URL de l'image de couverture
  likedCount: number,         // Nombre de likes
  venue: {
    name: string,
    adresse: string,
    coordinates: {
      latitude: number,
      longitude: number
    }
  },
  isSoldOut: boolean,         // Indique si l'événement est complet
  startAt: string,            // Date de début
  lowestPrice: number,        // Prix le plus bas en centimes
  description: string
}>
```

---

### 2. Récupérer un événement par son ID
**Route:** `GET /events/:id`

**Paramètres URL:**
- `id`: Identifiant de l'événement

**Réponse:** `200 OK`
```typescript
{
  id: string,
  title: string,
  cover: string,
  likedCount: number,
  venue: {
    name: string,
    adresse: string,
    coordinates: {
      latitude: number,
      longitude: number
    }
  },
  isSoldOut: boolean,
  startAt: string,
  lowestPrice: number,
  description: string
}
```

---

### 3. Récupérer plusieurs événements par leurs IDs
**Route:** `GET /events`

**Query Parameters:**
```typescript
{
  ids: string  // Liste d'IDs séparés par des virgules (ex: "id1,id2,id3")
}
```

**Réponse:** `200 OK`
```typescript
Array<Event>  // Même structure qu'un événement unique
```

---

### 4. Rechercher des événements
**Route:** `GET /events/search`

**Query Parameters:**
```typescript
{
  lat: number,      // Latitude de recherche
  lng: number,      // Longitude de recherche
  radius: number,   // Rayon de recherche en kilomètres
  query: string     // Terme de recherche
}
```

**Réponse:** `200 OK`
```typescript
Array<Event>  // Liste d'événements correspondants
```

---

### 5. Récupérer les événements likés par l'utilisateur
**Route:** `GET /events/liked`

**Headers requis:**
```
Authorization: Bearer <token>
```

**Réponse:** `200 OK`
```typescript
Array<Event>  // Liste des événements likés
```

---

## API Likes

### 1. Liker un événement
**Route:** `POST /events/:eventId/like`

**Paramètres URL:**
- `eventId`: Identifiant de l'événement

**Headers requis:**
```
Authorization: Bearer <token>
```

**Réponse:** `200 OK`
```typescript
void  // Pas de corps de réponse
```

---

### 2. Unliker un événement
**Route:** `DELETE /events/:eventId/like`

**Paramètres URL:**
- `eventId`: Identifiant de l'événement

**Headers requis:**
```
Authorization: Bearer <token>
```

**Réponse:** `200 OK`
```typescript
void  // Pas de corps de réponse
```

---

## API Tickets

### 1. Récupérer les tickets d'un événement
**Route:** `GET /events/:eventId/tickets`

**Paramètres URL:**
- `eventId`: Identifiant de l'événement

**Réponse:** `200 OK`
```typescript
Array<{
  id: string,
  eventId: string,
  name: string,      // Nom du type de ticket (ex: "Early Bird", "VIP")
  price: number,     // Prix en centimes
  quantity: number   // Quantité disponible
}>
```

---

## API Réservations

### 1. Créer une réservation
**Route:** `POST /bookings`

**Headers requis:**
```
Authorization: Bearer <token>
```

**Corps de la requête:**
```typescript
{
  items: Array<{
    id: string,        // ID du ticket
    quantity: number   // Quantité à réserver
  }>
}
```

**Réponse:** `200 OK`
```typescript
{
  id: string  // ID de la réservation créée
}
```

---

### 2. Récupérer une réservation par son ID
**Route:** `GET /bookings/:id`

**Paramètres URL:**
- `id`: Identifiant de la réservation

**Headers requis:**
```
Authorization: Bearer <token>
```

**Réponse:** `200 OK`
```typescript
{
  id: string,
  eventId: string,
  bookedAt: string,           // Date de réservation
  lines: Array<{
    id: string,
    name: string,             // Nom du ticket
    quantity: number,
    price: number             // Prix unitaire en centimes
  }>,
  status: 'requested' | 'reserved' | 'rejected' | 'awaiting-payment' | 'expired' | 'confirmed',
  total: number,              // Total en centimes
  fees: number,               // Frais en centimes
  clientSecret?: string       // Secret client pour le paiement Stripe (optionnel)
}
```

**Réponse:** `404 Not Found`
Si la réservation n'existe pas.

---

### 3. Récupérer toutes les réservations de l'utilisateur
**Route:** `GET /bookings/user`

**Headers requis:**
```
Authorization: Bearer <token>
```

**Réponse:** `200 OK`
```typescript
Array<Booking>  // Liste de réservations (même structure que ci-dessus)
```

---

### 4. Stream des mises à jour de réservation (SSE)
**Route:** `GET /bookings/stream`

**Headers requis:**
```
Authorization: Bearer <token>
```

**Type:** Server-Sent Events (SSE)

**Événements envoyés:**

**Événement:** `booking_updated`
```typescript
{
  id: string,
  eventId: string,
  bookedAt: string,
  lines: Array<{
    id: string,
    name: string,
    quantity: number,
    price: number
  }>,
  status: 'requested' | 'reserved' | 'rejected' | 'awaiting-payment' | 'expired' | 'confirmed',
  total: number,
  fees: number,
  clientSecret?: string
}
```

**Note:** Ce endpoint utilise Server-Sent Events (SSE) pour envoyer en temps réel les mises à jour de statut des réservations à l'utilisateur connecté.

---

## Authentification

Tous les endpoints marqués avec "Headers requis" nécessitent un token d'authentification JWT dans le header `Authorization`.

**Format du header:**
```
Authorization: Bearer <votre_token_jwt>
```

---

## Notes importantes

### Statuts de réservation
1. **requested** : Réservation demandée
2. **reserved** : Tickets réservés temporairement
3. **rejected** : Réservation refusée (manque de disponibilité)
4. **awaiting-payment** : En attente de paiement
5. **expired** : Réservation expirée
6. **confirmed** : Réservation confirmée après paiement
