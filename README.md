# AquaAlert

Sistema de monitoramento e alerta de enchentes para Android.

Aplicativo móvel que consome dados meteorológicos em tempo real para classificar o nível de risco de enchente na localização do usuário, exibir zonas de risco em mapa interativo e permitir o reporte colaborativo de ocorrências.


---

## Tecnologias

- Kotlin 1.9 + Android SDK 34
- Arquitetura MVVM com Repository Pattern
- Retrofit 2 + OkHttp 4 (consumo de API)
- API Open-Meteo (dados meteorológicos, gratuita, sem autenticação)
- Room Database (persistência local)
- OSMDroid 6.1.18 com OpenStreetMap (mapas, sem Google Play Services)
- Navigation Component, ViewBinding, LiveData, Coroutines
- Material Design 3

---

## Pré-requisitos

- Android Studio Hedgehog ou superior
- JDK 17
- Android SDK 34 instalado via SDK Manager
- Dispositivo ou emulador com Android 8.0 (API 26) ou superior
- O app **não** requer Google Play Services

---

## Como executar

**1. Clonar o repositório**

```bash
git clone https://github.com/meirameirameira/GlobalSolution12026.git
cd GlobalSolution12026
```

**2. Abrir o projeto no Android Studio**

Abra a pasta `AquaAlert/` (não a raiz do repositório).

```
File > Open > selecione a pasta AquaAlert/
```

**3. Sincronizar dependências**

O Android Studio solicitará automaticamente. Caso não ocorra:

```
File > Sync Project with Gradle Files
```

**4. Executar**

Conecte um dispositivo Android via USB com depuração habilitada, ou inicie um emulador (AVD ou Genymotion com Android 8+), e clique em **Run > Run 'app'**.

A primeira execução pode demorar alguns minutos enquanto o Gradle baixa as dependências.

---

## Permissões necessárias

O app solicitará permissão de localização na primeira abertura. Sem ela, o mapa usa São Paulo como localização padrão e os dados climáticos são carregados para essa coordenada.

---

## Estrutura do projeto

```
AquaAlert/
  app/src/main/java/br/com/fiap/aquaalert/
    data/
      api/          Retrofit + WeatherService
      db/           Room Database, DAOs, entidades
      model/        FloodAlert, FloodReport
      repository/   WeatherRepository, AlertRepository, ReportRepository
    ui/
      home/         HomeFragment + HomeViewModel
      map/          MapFragment + MapViewModel
      alerts/       AlertsFragment + AlertsViewModel
      report/       ReportFragment + ReportViewModel
    MainActivity.kt
```

---
