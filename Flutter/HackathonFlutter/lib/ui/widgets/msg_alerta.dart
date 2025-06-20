import 'package:flutter/material.dart';

class MsgAlerta {
  Future show({required BuildContext context,
    required String titulo, required String texto,
    List<Widget>? botoes
  }) {
    return showDialog(
      context: context,
      builder: (_) {
        return AlertDialog(
          title: Text(titulo),
          content: Text(texto),
          actions: _criarBotoes(context, botoes),
        );
      }
    );
  }

  List<Widget> _criarBotoes(BuildContext context, List<Widget>? botoes) {
    if (botoes == null) {
      return [
        TextButton(
          onPressed: () { Navigator.pop(context); },
          child: Text('Ok')
        )
      ];
    }

    return botoes;
  }
}