class Questao {
  final int numero; // Propriedade numero é um int
  final Map<String, String> alternativas;
  String? respostaSelecionada; // Pode ser String ou null

  Questao({
    required this.numero,
    required this.alternativas,
    this.respostaSelecionada,
  });

  factory Questao.fromJson(Map<String, dynamic> json) {
    return Questao(
      numero: json['numero'],
      alternativas: Map<String, String>.from(json['alternativas']),
      respostaSelecionada: json['respostaSelecionada'], // Pode ser null no JSON também
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