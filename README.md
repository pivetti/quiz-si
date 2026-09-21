# Quiz SI

Aplicativo Android em Java e XML com cinco perguntas, explicações e pontuação.
Gabriela Fogaça e Henrique Pivetti — Sistemas de Informação, IFPR Palmas.

## Executar

Abra a pasta do projeto no Android Studio, aguarde a sincronização do Gradle,
selecione um emulador ou celular e execute o módulo `app`.

Requisitos: JDK 17 ou 21, Android SDK 36 e Build Tools 35.0.0.
O aplicativo funciona em Android 8.0 ou superior.

Para gerar o APK pelo PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

O arquivo é gerado em `app/build/outputs/apk/debug/app-debug.apk`.

## Testes

```powershell
.\gradlew.bat testDebugUnitTest lintDebug
```

Com um emulador ou celular conectado:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```
