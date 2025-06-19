import 'package:camera/camera.dart';
import 'package:flutter/material.dart';
import 'package:hackathonflutter/Screens/result_screen.dart';
import 'package:image_picker/image_picker.dart';
import 'package:image/image.dart' as img;
import 'dart:io';
import '../services/ocr_service.dart';

class CameraScreen extends StatefulWidget {
  const CameraScreen({Key? key}) : super(key: key);

  @override
  State<CameraScreen> createState() => _CameraScreenState();
}

class _CameraScreenState extends State<CameraScreen> {
  CameraController? _controller;
  List<CameraDescription>? _cameras;
  XFile? _imageFile;
  final OcrService _ocrService = OcrService();

  @override
  void initState() {
    super.initState();
    _initializeCamera();
  }

  Future<void> _initializeCamera() async {
    _cameras = await availableCameras();
    if (_cameras != null && _cameras!.isNotEmpty) {
      _controller = CameraController(_cameras![0], ResolutionPreset.high);
      await _controller!.initialize();
      if (!mounted) {
        return;
      }
      setState(() {});
    }
  }

  Future<void> _takePicture() async {
    if (_controller == null || !_controller!.value.isInitialized) {
      return;
    }
    try {
      final image = await _controller!.takePicture();
      setState(() {
        _imageFile = image;
      });
      _processImage(image.path);
    } catch (e) {
      print(e);
    }
  }

  Future<void> _pickImage() async {
    final ImagePicker _picker = ImagePicker();
    final XFile? image = await _picker.pickImage(source: ImageSource.gallery);
    if (image != null) {
      setState(() {
        _imageFile = image;
      });
      _processImage(image.path);
    }
  }

  Future<void> _processImage(String imagePath) async {
    print('Image selected/captured: $imagePath');
    final extractedData = await _ocrService.processGabarito(imagePath);
    print('Extracted Data: $extractedData');

    // Navigate to ResultScreen with extracted data
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => ResultScreen(extractedData: extractedData),
      ),
    );

    // Example of loading an image with the 'image' package
    final originalImage = img.decodeImage(File(imagePath).readAsBytesSync());
    if (originalImage != null) {
      // You can perform operations like cropping or rotating here
      // For example, to rotate by 90 degrees:
      // final rotatedImage = img.copyRotate(originalImage, 90);
      // File('${imagePath}_rotated.jpg').writeAsBytesSync(img.encodeJpg(rotatedImage));
    }
  }

  @override
  void dispose() {
    _controller?.dispose();
    _ocrService.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (_controller == null || !_controller!.value.isInitialized) {
      return const Center(child: CircularProgressIndicator());
    }
    return Scaffold(
      appBar: AppBar(title: const Text('Escanear Gabarito')),
      body: Column(
        children: [
          Expanded(
            child: _imageFile == null
                ? CameraPreview(_controller!)
                : Image.file(File(_imageFile!.path)),
          ),
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                FloatingActionButton(
                  onPressed: _takePicture,
                  heroTag: 'takePicture',
                  child: const Icon(Icons.camera_alt),
                ),
                FloatingActionButton(
                  onPressed: _pickImage,
                  heroTag: 'pickImage',
                  child: const Icon(Icons.photo_library),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}


