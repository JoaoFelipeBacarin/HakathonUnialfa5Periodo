// lib/main.dart
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:hackathonflutter/services/auth_service.dart';
import 'package:hackathonflutter/services/api_service.dart';
import 'package:hackathonflutter/services/aluno_service.dart';
import 'package:hackathonflutter/services/avaliacao_service.dart';
import 'package:hackathonflutter/services/ocr_service.dart'; // ADICIONAR ESTE IMPORT
import 'package:hackathonflutter/ui/pages/splash_page.dart';
// import 'package:hackathonflutter/ui/widgets/msg_alerta.dart'; // Não é necessário aqui se for um utilitário estático

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        // 1. Prover AuthService (não depende de outros serviços aqui)
        Provider<AuthService>(
          create: (_) => AuthService(),
        ),
        // 2. Prover ApiService, que depende de AuthService
        ProxyProvider<AuthService, ApiService>(
          update: (_, authService, apiService) => ApiService(authService),
        ),
        // 3. Prover AlunoService, que depende de ApiService
        ProxyProvider<ApiService, AlunoService>(
          update: (_, apiService, previousAlunoService) => AlunoService(apiService),
        ),
        // 4. Prover AvaliacaoService, que depende de ApiService
        ProxyProvider<ApiService, AvaliacaoService>(
          update: (_, apiService, previousAvaliacaoService) => AvaliacaoService(apiService),
        ),
        // 5. ADICIONAR O OcrService
        Provider<OcrService>(
          create: (_) => OcrService(),
          dispose: (_, ocrService) => ocrService.dispose(), // Limpar recursos quando não precisar mais
        ),
        // Você pode adicionar outros providers aqui, como MsgAlerta se quiser injetá-lo.
        // Por enquanto, MsgAlerta é uma classe utilitária estática, não precisa de provider.
      ],
      child: MaterialApp(
        title: 'Sistema de Avaliação',
        theme: ThemeData(
          useMaterial3: true,
          colorScheme: ColorScheme.fromSeed(
            seedColor: Colors.blue,
            brightness: Brightness.light,
          ),
          appBarTheme: const AppBarTheme(
            centerTitle: true,
            elevation: 2,
          ),
          elevatedButtonTheme: ElevatedButtonThemeData(
            style: ElevatedButton.styleFrom(
              padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(8),
              ),
            ),
          ),
          cardTheme: CardTheme(
            elevation: 4,
            shape: RoundedRectangleBorder(
              borderRadius: BorderRadius.circular(12),
            ),
          ),
        ),
        home: const SplashPage(),
        debugShowCheckedModeBanner: false,
      ),
    );
  }
}