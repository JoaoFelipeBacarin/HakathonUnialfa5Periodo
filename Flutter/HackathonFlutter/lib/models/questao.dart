class Questao {
  final int numero;
  // Removido 'enunciado' conforme seu snippet mais recente de avaliacao_service
  final Map<String, String> alternativas;
  String? respostaSelecionada;

  Questao({
    required this.numero,
    required this.alternativas,
    this.respostaSelecionada,
  });

  factory Questao.fromJson(Map<String, dynamic> json) {
    return Questao(
      numero: json['numero'],
      alternativas: Map<String, String>.from(json['alternativas']),
      respostaSelecionada: json['respostaSelecionada'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'numero': numero,
      'alternativas': alternativas,
      'respostaSelecionada': respostaSelecionada,
    };
  }
}