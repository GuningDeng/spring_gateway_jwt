# spring_gateway_jwt

### Technologie-stack:
- Spring Cloud Gateway
- Spring Boot
- JWT
- JPA
- SQL database

### Cruciale functionaliteiten
1. Alle servers bevinden zich achter de gateway.

2. Alle servers zijn volledig ontkoppeld. Alle servers communiceren alleen met elkaar via de gateway. Servers weten niet van elkaars bestaan ​​af.

3. Dubbel token: Het "Refresh Token" heeft een korte geldigheidsduur, bijvoorbeeld 30 minuten. Als het "Refresh Token" niet binnen 30 minuten wordt bijgewerkt, verloopt de authenticatie van de gebruiker.

4. Met uitzondering van de authenticatiecentrumserver, weten andere servers niet van het bestaan ​​van het JWT af.

5. Servers die geen deel uitmaken van het authenticatiecentrum verkrijgen gebruikersinformatie uit de header (Header).

### Architectuur

![Architectuur](/images/gateway_jwt_architecture.jpg "Architectuur")

### Cruciale processen
**Het Inlogproces**

![Log in](/images/gateway_jwt_sq_inlog.jpg "Log in")

**Het proces van toegang tot een server**

![Access](/images/gateway_jwt_sq_access.jpg "Access")

**Het vernieuwingstokenproces**

![Refresh token](/images/gateway_jwt_sq_refresh_token.jpg "Refresh token")

###
Profijt: 
- Verminderde noodzaak voor het herhaaldelijk laden en verifiëren van sleutels en JWT's.
- Authenticatielogica hoeft niet afzonderlijk in elke service te worden geïmplementeerd. De gateway verzorgt de uniforme authenticatie.
