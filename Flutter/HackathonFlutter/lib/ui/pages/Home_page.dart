// home_page.dart
import 'package:flutter/material.dart';
import 'package:hackathonflutter/services/auth_service.dart';
import 'package:hackathonflutter/ui/pages/listagem_page.dart';
import 'package:hackathonflutter/ui/pages/login_page.dart';
import 'package:hackathonflutter/screens/camera_screen.dart'; // Importe CameraScreen

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;
    final screenHeight = MediaQuery.of(context).size.height;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Menu Principal'),
        automaticallyImplyLeading: false,
      ),
      body: SafeArea(
        child: SingleChildScrollView(
          child: Padding(
            padding: EdgeInsets.symmetric(
              horizontal: screenWidth * 0.05, // 5% das bordas
              vertical: 20.0,
            ),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: <Widget>[
                SizedBox(height: screenHeight * 0.02),

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
                SizedBox(height: screenHeight * 0.05),

                _buildFeatureButton(
                  context,
                  text: 'Selecionar Aluno',
                  description: 'Lançar ou editar gabarito de um aluno específico.',
                  icon: Icons.person,
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => const ListagemPage(),
                      ),
                    );
                  },
                ),
                SizedBox(height: screenHeight * 0.025),

                _buildFeatureButton(
                  context,
                  text: 'Visualizar Gabarito Correto',
                  description: 'Consultar o gabarito oficial de uma prova.',
                  icon: Icons.assignment_turned_in,
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => const ListagemPage(isViewingGabarito: true),
                      ),
                    );
                  },
                ),
                SizedBox(height: screenHeight * 0.025),

                _buildFeatureButton(
                  context,
                  text: 'Escanear Gabarito',
                  description: 'Usar a câmera para escanear um gabarito de prova.',
                  icon: Icons.camera_alt,
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => const CameraScreen(),
                      ),
                    );
                  },
                ),
                SizedBox(height: screenHeight * 0.06),

                // Botão de Sair/Trocar de Usuário - Otimizado para mobile
                _buildLogoutButton(context),

                SizedBox(height: screenHeight * 0.02),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildFeatureButton(BuildContext context, {
    required String text,
    required String description,
    required IconData icon,
    required VoidCallback onTap,
  }) {
    final screenWidth = MediaQuery.of(context).size.width;

    return Container(
      constraints: BoxConstraints(
        minHeight: screenWidth * 0.35, // Altura mínima responsiva
        maxHeight: 200, // Altura máxima para evitar botões muito grandes
      ),
      child: Card(
        elevation: 6,
        shadowColor: Colors.grey.withOpacity(0.3),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(16),
        ),
        child: InkWell(
          onTap: onTap,
          borderRadius: BorderRadius.circular(16),
          splashColor: Theme.of(context).primaryColor.withOpacity(0.1),
          highlightColor: Theme.of(context).primaryColor.withOpacity(0.05),
          child: Padding(
            padding: EdgeInsets.symmetric(
              horizontal: screenWidth * 0.04,
              vertical: 20.0,
            ),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Container(
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: Theme.of(context).primaryColor.withOpacity(0.1),
                    borderRadius: BorderRadius.circular(50),
                  ),
                  child: Icon(
                    icon,
                    size: screenWidth * 0.08, // Ícone responsivo
                    color: Theme.of(context).primaryColor,
                  ),
                ),
                SizedBox(height: screenWidth * 0.03),

                Text(
                  text,
                  textAlign: TextAlign.center,
                  style: TextStyle(
                    fontSize: screenWidth * 0.045, // Texto responsivo
                    fontWeight: FontWeight.bold,
                    color: Colors.black87,
                  ),
                ),
                SizedBox(height: screenWidth * 0.02),

                Flexible(
                  child: Text(
                    description,
                    textAlign: TextAlign.center,
                    maxLines: 3,
                    overflow: TextOverflow.ellipsis,
                    style: TextStyle(
                      fontSize: screenWidth * 0.035, // Descrição responsiva
                      color: Colors.grey[600],
                      height: 1.3,
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildLogoutButton(BuildContext context) {
    final screenWidth = MediaQuery.of(context).size.width;

    return Container(
      width: double.infinity,
      constraints: BoxConstraints(
        maxWidth: screenWidth * 0.8, // Máximo 80% da largura da tela
      ),
      child: OutlinedButton.icon(
        onPressed: () => _confirmLogout(context),
        icon: const Icon(
          Icons.logout,
          color: Colors.red,
          size: 20,
        ),
        label: Text(
          'Sair / Trocar de Usuário',
          style: TextStyle(
            color: Colors.red,
            fontSize: screenWidth * 0.038, // Texto responsivo
            fontWeight: FontWeight.w600,
          ),
        ),
        style: OutlinedButton.styleFrom(
          padding: EdgeInsets.symmetric(
            horizontal: screenWidth * 0.05,
            vertical: 16,
          ),
          side: const BorderSide(
            color: Colors.red,
            width: 1.5,
          ),
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
          backgroundColor: Colors.red.withOpacity(0.02),
        ),
      ),
    );
  }

  void _confirmLogout(BuildContext context) {
    showDialog(
      context: context,
      builder: (BuildContext dialogContext) {
        return AlertDialog(
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(16),
          ),
          title: const Text(
            'Confirmação',
            style: TextStyle(fontWeight: FontWeight.bold),
          ),
          content: const Text(
            'Deseja realmente sair ou trocar de usuário?',
            style: TextStyle(fontSize: 16),
          ),
          actions: <Widget>[
            TextButton(
              child: Text(
                'Cancelar',
                style: TextStyle(
                  color: Colors.grey[600],
                  fontWeight: FontWeight.w500,
                ),
              ),
              onPressed: () {
                Navigator.of(dialogContext).pop();
              },
            ),
            ElevatedButton(
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.red,
                foregroundColor: Colors.white,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(8),
                ),
              ),
              child: const Text(
                'Sair',
                style: TextStyle(fontWeight: FontWeight.w600),
              ),
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