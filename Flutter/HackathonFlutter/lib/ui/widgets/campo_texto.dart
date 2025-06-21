import 'package:flutter/material.dart';

class CampoTexto extends StatelessWidget {
  final TextEditingController controller;
  final String? texto;
  final TextInputType? teclado;
  final bool? isHabilitado;
  final bool? isObscureText;
  final ValueChanged<String>? onChanged;

  const CampoTexto({
    required this.controller,
    this.texto,
    this.teclado,
    this.isHabilitado,
    this.isObscureText = false,
    this.onChanged,
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return TextField( // Removido o Padding. Agora o pai controla o espaçamento.
      enabled: isHabilitado,
      controller: controller,
      keyboardType: teclado,
      obscureText: isObscureText!,
      onChanged: onChanged,
      decoration: InputDecoration(
        labelText: texto,
        border: const OutlineInputBorder(),
      ),
    );
  }
}