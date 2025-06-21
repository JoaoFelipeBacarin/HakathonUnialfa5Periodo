import 'package:flutter/material.dart';
import 'package:hackathonflutter/services/auth_service.dart';
import 'package:hackathonflutter/ui/widgets/barra_titulo.dart';
import 'package:hackathonflutter/ui/widgets/botao_quadrado.dart';
import 'package:hackathonflutter/ui/widgets/campo_texto.dart'; // Importante: Garanta que esta é a versão atualizada
import 'package:hackathonflutter/ui/pages/home_page.dart';
import 'package:hackathonflutter/ui/widgets/msg_alerta.dart';
import 'package:hackathonflutter/ui/widgets/circulo_espera.dart';
import 'package:hackathonflutter/extensions/string_extension.dart';

class LoginPage extends StatefulWidget {
  const LoginPage({super.key});

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final TextEditingController _emailController = TextEditingController();
  final TextEditingController _senhaController = TextEditingController();
  final AuthService _authService = AuthService();
  bool _carregando = false;

  Future<void> _fazerLogin() async {
    final email = _emailController.text.trim();
    final senha = _senhaController.text.trim();

    if (email.isEmpty || senha.isEmpty) {
      MsgAlerta().show(
        context: context,
        titulo: 'Campos Obrigatórios',
        texto: 'Por favor, preencha todos os campos.',
      );
      return;
    }

    if (!email.isValidEmail) {
      MsgAlerta().show(
        context: context,
        titulo: 'E-mail Inválido',
        texto: 'Por favor, insira um e-mail válido.',
      );
      return;
    }

    setState(() {
      _carregando = true;
    });

    try {
      final sucesso = await _authService.autenticar(email, senha);

      if (mounted) {
        setState(() {
          _carregando = false;
        });

        if (sucesso) {
          Navigator.pushReplacement(
            context,
            MaterialPageRoute(builder: (context) => const HomePage()),
          );
        } else {
          MsgAlerta().show(
            context: context,
            titulo: 'Erro de Login',
            texto: 'E-mail ou senha inválidos. Tente novamente.',
          );
        }
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _carregando = false;
        });

        MsgAlerta().show(
          context: context,
          titulo: 'Erro',
          texto: 'Ocorreu um erro ao tentar fazer login. Verifique sua conexão e tente novamente. Detalhes: $e',
        );
      }
    }
  }

  @override
  void dispose() {
    _emailController.dispose();
    _senhaController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: BarraTitulo.criar('Login'),
      body: _carregando
          ? const CirculoEspera()
          : Center(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(16.0), // Padding geral para a tela
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              Icon(
                Icons.person_pin_outlined,
                size: 100,
                color: Theme.of(context).colorScheme.primary,
              ),
              const SizedBox(height: 40),

              // E-mail field
              Padding( // Adicionado Padding ao CampoTexto individualmente
                padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
                child: CampoTexto(
                  controller: _emailController,
                  texto: 'E-mail',
                  teclado: TextInputType.emailAddress,
                  isHabilitado: true,
                ),
              ),
              // SizedBox(height: 10), // Removido, pois o padding no CampoTexto já controla isso

              // Password field
              Padding( // Adicionado Padding ao CampoTexto individualmente
                padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
                child: CampoTexto(
                  controller: _senhaController,
                  texto: 'Senha',
                  teclado: TextInputType.text,
                  isObscureText: true,
                  isHabilitado: true,
                ),
              ),
              const SizedBox(height: 30), // Mantenha este para espaçamento com o botão

              // Login Button
              Padding( // Adicionado Padding para o botão também
                padding: const EdgeInsets.symmetric(horizontal: 16.0),
                child: BotaoQuadrado(
                  texto: 'ENTRAR',
                  acao: _fazerLogin,
                  corFundo: Theme.of(context).colorScheme.primary,
                  corTexto: Colors.white,
                ),
              ),
              const SizedBox(height: 20),

              TextButton(
                onPressed: () {
                  MsgAlerta().show(
                    context: context,
                    titulo: 'Funcionalidade Futura',
                    texto: 'A recuperação de senha ainda não foi implementada.',
                  );
                },
                child: Text(
                  'Esqueceu sua senha?',
                  style: TextStyle(color: Theme.of(context).colorScheme.secondary),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}