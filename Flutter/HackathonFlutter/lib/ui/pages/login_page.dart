import 'package:flutter/material.dart';
import 'package:hackathonflutter/services/auth_service.dart';
import 'package:hackathonflutter/ui/widgets/barra_titulo.dart';
import 'package:hackathonflutter/ui/widgets/botao_quadrado.dart';
import 'package:hackathonflutter/ui/widgets/campo_texto.dart';
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

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: BarraTitulo.criar('Login'),
      body: _carregando
          ? const CirculoEspera()
          : Center(
        child: SingleChildScrollView(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: <Widget>[
              const Icon(
                Icons.account_circle,
                size: 100,
                color: Colors.blue,
              ),
              const SizedBox(height: 32.0),

              CampoTexto(
                controller: _emailController,
                texto: 'E-mail ou Usuário',
                teclado: TextInputType.emailAddress,
              ),
              const SizedBox(height: 16.0),

              CampoTexto(
                controller: _senhaController,
                texto: 'Senha',
                teclado: TextInputType.text,
                isObscureText: true,
              ),
              const SizedBox(height: 32.0),

              BotaoQuadrado(
                clique: _realizarLogin,
                texto: 'Entrar',
                icone: Icons.login,
              ),

              const SizedBox(height: 16.0),

              Text(
                'Use: teste@email.com / 12345',
                style: TextStyle(
                  color: Colors.grey[600],
                  fontSize: 12,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Future<void> _realizarLogin() async {
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
          texto: 'Ocorreu um erro durante o login. Tente novamente.',
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
}