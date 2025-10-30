# SDV Developer Console - Login Credentials

## 🔑 User Credentials

You can login to the SDV Developer Console at **http://localhost:3000** using any of these accounts:

### Option 1: Developer Account (Recommended)
- **Username:** `developer`
- **Password:** `password`
- **Role:** USER

### Option 2: DCO Account
- **Username:** `dco`
- **Password:** `dco`
- **Role:** USER

### Option 3: Admin Account (Full Access)
- **Username:** `admin`
- **Password:** `password`
- **Roles:** USER, ADMIN

---

## 📋 System Status

All services are running and healthy:

| Service | Status | URL |
|---------|--------|-----|
| Developer Console UI | ✅ Running | http://localhost:3000 |
| DCO Gateway | ✅ Running | http://localhost:8080 |
| Scenario Library Service | ✅ Running | http://localhost:8082 |
| Tracks Management Service | ✅ Running | http://localhost:8081 |
| PostgreSQL | ✅ Running | localhost:5432 |
| Minio S3 | ✅ Running | http://localhost:9001 |
| Redis | ✅ Running | localhost:6379 |

---

## 🔧 Troubleshooting Login Issues

If you're still having trouble logging in:

1. **Clear browser cache and cookies** for localhost:3000
2. **Try a different browser** or incognito/private mode
3. **Check browser console** (F12) for any JavaScript errors
4. **Verify the UI is loading** by opening http://localhost:3000 in your browser

### Test Backend Authentication (Terminal):
```bash
# Test if authentication works
curl http://localhost:8080/graphql \
  -H "Authorization: Basic $(echo -n 'developer:password' | base64)" \
  -H "Content-Type: application/json" \
  -d '{"query":"{ __typename }"}'
```

Expected response: `{"data":{"__typename":"Query"}}`

---

## 📱 Quick Access Links

- **Main Application**: http://localhost:3000
- **GraphQL Playground**: http://localhost:8080/playground
- **Scenario Service Swagger**: http://localhost:8082/openapi/swagger-ui/index.html
- **Tracks Service Swagger**: http://localhost:8081/openapi/swagger-ui/index.html
- **Database Admin (pgAdmin)**: http://localhost:5050
  - Username: `admin@default.com`
  - Password: `admin`
- **Minio Console**: http://localhost:9001
  - Access Key: `V37k8prc3O8MLdGx`
  - Secret Key: `HHm3CuzWnduZJh7lS9gQJjwj683AJlK1`

---

## 🎯 Next Steps

1. Open http://localhost:3000 in your browser
2. Use **developer** / **password** to login
3. Start creating scenarios and tracks!

If you continue to experience issues, please check the browser console (F12) and share any error messages.
