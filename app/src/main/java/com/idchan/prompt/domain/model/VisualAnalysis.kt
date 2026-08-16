package com.idchan.prompt.domain.model

data class VisualAnalysis(
    val subject: SubjectAnalysis = SubjectAnalysis(),
    val environment: EnvironmentAnalysis = EnvironmentAnalysis(),
    val composition: CompositionAnalysis = CompositionAnalysis(),
    val camera: CameraAnalysis = CameraAnalysis(),
    val lighting: LightingAnalysis = LightingAnalysis(),
    val color: ColorAnalysis = ColorAnalysis(),
    val style: StyleAnalysis = StyleAnalysis(),
    val textureQuality: TextureQualityAnalysis = TextureQualityAnalysis()
)

data class SubjectAnalysis(
    val mainSubject: String = "",
    val subjectCount: Int = 1,
    val ageAppearance: String = "",
    val physicalCharacteristics: String = "",
    val faceAndExpression: String = "",
    val hairStyleAndColor: String = "",
    val skinAppearance: String = "",
    val clothingAndMaterials: String = "",
    val clothingColors: String = "",
    val accessoriesAndJewelry: String = "",
    val poseAndBodyPosition: String = "",
    val handAndHeadPosition: String = "",
    val eyeDirection: String = "",
    val objectInteraction: String = ""
)

data class EnvironmentAnalysis(
    val locationType: String = "", // Indoor/Outdoor/Studio
    val backgroundElements: String = "",
    val architectureAndSurfaces: String = "",
    val furnitureAndProps: String = "",
    val plantsAndNature: String = "",
    val foregroundElements: String = "",
    val environmentDetails: String = ""
)

data class CompositionAnalysis(
    val orientationAndAspect: String = "",
    val subjectPlacement: String = "", // e.g. "slightly left of center"
    val subjectScaleAndDistance: String = "",
    val headroomAndNegativeSpace: String = "",
    val framingAndSymmetry: String = "",
    val depthAndSpatialLayers: String = ""
)

data class CameraAnalysis(
    val cameraAngle: String = "", // Eye level, low angle, high angle, bird's eye
    val shotType: String = "", // Close-up, medium shot, full-body, wide shot
    val focalLengthPerspective: String = "", // e.g. "85mm portrait-lens perspective"
    val depthOfField: String = "", // Shallow depth of field, sharp foreground, soft bokeh
    val focusArea: String = ""
)

data class LightingAnalysis(
    val primaryLightSource: String = "", // Key light, natural sunlight, neon, studio softbox
    val lightDirection: String = "", // Left rim light, top-down illumination, backlit
    val lightQuality: String = "", // Hard light, soft diffused light
    val shadowsAndHighlights: String = "",
    val ambientMood: String = ""
)

data class ColorAnalysis(
    val dominantColors: String = "",
    val secondaryColors: String = "",
    val colorTemperature: String = "", // Warm, cool, neutral
    val saturationAndContrast: String = "",
    val colorGrading: String = ""
)

data class StyleAnalysis(
    val primaryStyle: String = "", // Photorealistic photography, cinematic, editorial, anime, 3d render
    val artStyleDetails: String = "",
    val aestheticGenre: String = ""
)

data class TextureQualityAnalysis(
    val skinAndFabricTexture: String = "",
    val sharpnessAndDetail: String = "",
    val grainAndDynamicRange: String = "",
    val overallRealism: String = ""
)
