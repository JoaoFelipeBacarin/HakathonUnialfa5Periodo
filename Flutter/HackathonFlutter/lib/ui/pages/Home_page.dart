// home_page.dart
import 'package:flutter/material.dart';
import 'package:hackathonflutter/services/auth_service.dart';
import 'package:hackathonflutter/ui/pages/listagem_page.dart'; // Importe ListagemPage
import 'package:hackathonflutter/ui/pages/login_page.dart';

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Menu Principal'),
        automaticallyImplyLeading: false,
      ),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(20.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: <Widget>[
              const Icon(
                Icons.school,
                size: 80,
                color: Colors.blue,
              ),
              const SizedBox(height: 20),
              const Text(
                'Sistema de Avaliação',
                textAlign: TextAlign.center,
                style: TextStyle(
                  fontSize: 24,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 40),

              _buildFeatureButton(
                context,
                text: 'Selecionar Aluno',
                description: 'Lançar ou editar gabarito de um aluno.',
                icon: Icons.person_add,
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                        builder: (context) => const ListagemPage(isViewingGabarito: false)), // Modo padrão
                  );
                },
              ),
              const SizedBox(height: 20),
              _buildFeatureButton(
                context,
                text: 'Ver Gabarito',
                description: 'Visualizar gabarito oficial de uma prova.',
                icon: Icons.assignment_turned_in,
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                        builder: (context) => const ListagemPage(isViewingGabarito: true)), // Modo de visualização de gabarito
                  );
                },
              ),
              // REMOVIDO: const SizedBox(height: 20),
              // REMOVIDO: _buildFeatureButton(
              // REMOVIDO:   context,
              // REMOVIDO:   text: 'Resultados e Relatórios',
              // REMOVIDO:   description: 'Acompanhe o desempenho de alunos e turmas.',
              // REMOVIDO:   icon: Icons.bar_chart,
              // REMOVIDO:   onTap: () {
              // REMOVIDO:     // Funcionalidade removida
              // REMOVIDO:   },
              // REMOVIDO: ),
              const SizedBox(height: 40), // Mantido para espaçamento
              ElevatedButton.icon(
                onPressed: () => _confirmLogout(context),
                icon: const Icon(Icons.logout),
                label: const Text('Sair / Trocar de Usuário'),
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.red.shade400,
                  foregroundColor: Colors.white,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildFeatureButton(BuildContext context,
      {required String text,
        required String description,
        required IconData icon,
        required VoidCallback onTap}) {
    return Card(
      elevation: 5,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(15),
      ),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(15),
        child: Padding(
          padding: const EdgeInsets.all(20.0),
          child: Column(
            children: [
              Icon(
                icon,
                size: 40,
                color: Theme.of(context).colorScheme.primary,
              ),
              const SizedBox(height: 10),
              Text(
                text,
                textAlign: TextAlign.center,
                style: const TextStyle(
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 8),
              Text(
                description,
                textAlign: TextAlign.center,
                style: TextStyle(
                  fontSize: 14,
                  color: Colors.grey[600],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  void _confirmLogout(BuildContext context) {
    showDialog(
      context: context,
      builder: (BuildContext dialogContext) {
        return AlertDialog(
          title: const Text('Confirmação'),
          content: const Text('Deseja realmente sair ou trocar de usuário?'),
          actions: <Widget>[
            TextButton(
              child: const Text('Cancelar'),
              onPressed: () {
                Navigator.of(dialogContext).pop();
              },
            ),
            TextButton(
              child: const Text('Sair'),
              onPressed: () async {
                Navigator.of(dialogContext).pop();
                await AuthService().logout();
                if (context.mounted) {
                  Navigator.pushReplacement(
                    context,
                    MaterialPageRoute(builder: (context) => const LoginPage()),
                  );
                }
              },
            ),
          ],
        );
      },
    );
  }
}