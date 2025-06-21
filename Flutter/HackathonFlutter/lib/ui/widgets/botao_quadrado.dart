// lib/ui/widgets/botao_quadrado.dart (Versão Presumida e Corrigida)
import 'package:flutter/material.dart';

class BotaoQuadrado extends StatelessWidget {
  final String texto;
  final VoidCallback acao;
  final Color? corFundo;
  final Color? corTexto;
  final IconData? icone; // Se você tiver ícones no botão

  const BotaoQuadrado({
    required this.texto,
    required this.acao,
    this.corFundo,
    this.corTexto,
    this.icone,
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return SizedBox( // <--- ADICIONAR ISSO PARA FORÇAR A LARGURA TOTAL
      width: double.infinity, // <--- Ocupa toda a largura disponível
      child: ElevatedButton(
        onPressed: acao,
        style: ElevatedButton.styleFrom(
          backgroundColor: corFundo ?? Theme.of(context).colorScheme.primary,
          foregroundColor: corTexto ?? Colors.white,
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 15), // Ajuste o padding interno se necessário
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(8), // Se quiser bordas arredondadas
          ),
          textStyle: const TextStyle(
            fontSize: 18,
            fontWeight: FontWeight.bold,
          ),
          // ADICIONAR ISTO PARA GARANTIR QUE O BOTÃO SE EXPANDA
          minimumSize: const Size.fromHeight(50), // Garante uma altura mínima e expande largura
        ),
        child: icone != null
            ? Row(
          mainAxisSize: MainAxisSize.min, // Mantém o Row compacto
          children: [
            Icon(icone),
            const SizedBox(width: 8),
            Text(texto),
          ],
        )
            : Text(texto),
      ),
    );
  }
}