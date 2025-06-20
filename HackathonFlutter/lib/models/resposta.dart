class Resposta {
  final int alunoId;
  final int questaoNumero; // Propriedade questaoNumero é um int
  final String alternativaSelecionada; // Propriedade alternativaSelecionada é uma String
  final DateTime dataResposta;

  Resposta({
    required this.alunoId,
    required this.questaoNumero,
    required this.alternativaSelecionada,
    required this.dataResposta,
  });

  factory Resposta.fromJson(Map<String, dynamic> json) {
    return Resposta(
      alunoId: json['alunoId'],
      questaoNumero: json['questaoNumero'],
      alternativaSelecionada: json['alternativaSelecionada'],
      dataResposta: DateTime.parse(json['dataResposta']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'alunoId': alunoId,
      'questaoNumero': questaoNumero,
      'alternativaSelecionada': alternativaSelecionada,
      'dataResposta': dataResposta.toIso8601String(),
    };
  }
}