import 'package:flutter/material.dart';

class BotaoFlutuante extends StatelessWidget {
  final VoidCallback evento;
  final IconData icone;

  const BotaoFlutuante({required this.evento,
    required this.icone, super.key});

  @override
  Widget build(BuildContext context) {
    return FloatingActionButton(
      onPressed: evento,
      backgroundColor: Colors.purple.shade200,
      foregroundColor: Colors.purple.shade900,
      child: Icon(icone, size: 32),
    );
  }
}
