import 'package:flutter/material.dart';

class CampoTexto extends StatelessWidget {
  final TextEditingController controller;
  final String? texto;
  final TextInputType? teclado;
  final bool? isHabilitado;
  final bool? isObscureText; // Nova propriedade para ocultar texto

  const CampoTexto({
    required this.controller,
    this.texto,
    this.teclado,
    this.isHabilitado,
    this.isObscureText = false, // Valor padrão para não ocultar
    super.key
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.all(16),
      child: TextField(
        enabled: isHabilitado,
        controller: controller,
        keyboardType: teclado,
        obscureText: isObscureText!, // Usando a nova propriedade
        decoration: InputDecoration(
            labelText: texto,
            border: const OutlineInputBorder()
        ),
      ),
    );
  }
}