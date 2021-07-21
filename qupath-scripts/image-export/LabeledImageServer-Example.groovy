//Example of using the LabeledImageServer to export regions (not full images)
// plus exporting the original image and overlay

// 1 is full resolution. You may want something more like 20 or higher for small thumbnails
downsample = 1 
//remove the findAll to get all annotations, or change the null to getPathClass("Tumor") to only export Tumor annotations
annotations = getAnnotationObjects().findAll{it.getPathClass() == null}

def imageName = GeneralTools.getNameWithoutExtension(getCurrentImageData().getServer().getMetadata().getName())
def imageData = getCurrentImageData()
//Make sure the location you want to save the files to exists - requires a Project
def pathOutput = buildFilePath(PROJECT_BASE_DIR, 'image_export')
mkdirs(pathOutput)
def cellLabelServer = new LabeledImageServer.Builder(imageData)
    .backgroundLabel(0, ColorTools.WHITE) // Specify background label (usually 0 or 255)
    .useCells()
    .useUniqueLabels()
    .downsample(downsample)    // Choose server resolution; this should match the resolution at which tiles are exported    
    .multichannelOutput(false) // If true, each label refers to the channel of a multichannel binary image (required for multiclass probability)
    .build()
def annotationLabelServer = new LabeledImageServer.Builder(imageData)
    .backgroundLabel(0, ColorTools.WHITE) // Specify background label (usually 0 or 255)
    .addLabel('Tumor',1) //Each class requires a name and a number
    .downsample(downsample)    // Choose server resolution; this should match the resolution at which tiles are exported    
    .multichannelOutput(false) // If true, each label refers to the channel of a multichannel binary image (required for multiclass probability)
    .build()



annotations.eachWithIndex{anno,x->
    roi = anno.getROI()
    def requestROI = RegionRequest.createInstance(getCurrentServer().getPath(), 1, roi)
   
    pathOutput = buildFilePath(PROJECT_BASE_DIR, 'image_export', imageName+"_region_"+x)
    //Now to export one image of each type per annotation (in the default case, unclassified
    //Comment out the lines you do not want to export.
    //objects with overlays as seen in the Viewer    
    writeRenderedImageRegion(getCurrentViewer(), requestROI, pathOutput+"_rendered.tif")

    //Labeled images, either cells or annotations
    writeImageRegion(annotationLabelServer, requestROI, pathOutput+"_annotationLabels.tif")
    writeImageRegion(cellLabelServer, requestROI, pathOutput+"_cellLabels.tif")
    
    //To get the image behind the objects, you would simply use writeImageRegion
    writeImageRegion(getCurrentServer(), requestROI, pathOutput+"_original.tif")

}    
    