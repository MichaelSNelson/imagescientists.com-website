//For QuPath V0.3.0
//Modified by Michael S Nelson, July 2021
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Define the color map name
String colorMapName = "Viridis"
// Define measurement & display range
def measurement = "Nucleus: Circularity" // Set to null to reset

//SETTING MIN AND MAX IS VERY IMPORTANT -
//THEY WILL CHANGE GREATLY DEPENDING ON THE MEASUREMENT SELECTED
double minValue = 0.0
double maxValue = 1.0
// It is important to define the downsample!
// This is required to determine annotation line thicknesses
double downsample = 20
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////



//Leave this section uncommented to access default color maps
////////////////////////////////////////////////////////////////////
colorMapper = ColorMaps.getColorMaps().find {it.getKey() == colorMapName}.getValue()
////////////////////////////////////////////////////////////////////


//Use this section to find the color map if you have created your own colormap
////////////////////////////////////////////////////////////////////
//String userPath = PathPrefs.getUserPath();
//Path dirUser = Paths.get(userPath, "colormaps");
//colorMapper =ColorMaps.loadColorMapsFromDirectory(dirUser).find {it.getName() == colorMapName}
////////////////////////////////////////////////////////////////////

//Great, we have the color map at this point, if it exists.

def viewer = getCurrentViewer()
def options = viewer.getOverlayOptions()
def detections = getDetectionObjects()
def imageName = GeneralTools.getNameWithoutExtension(getProjectEntry().getImageName())

//Two options here, the one that works but requires "index" or a specific measurement, and the one that tries to find out what the currently selected measurement in the dialog is. The latter fails.
//def mapper = options.getMeasurementMapper()
def mapper = new MeasurementMapper(colorMapper, measurement, detections)
mapper.setDisplayMinValue(minValue)
mapper.setDisplayMaxValue(maxValue)
options.setMeasurementMapper(mapper)

// Now export the rendered image


// Add the output file path here
String path = buildFilePath(PROJECT_BASE_DIR, 'rendered')
mkdirs(path)

// Request the current viewer for settings, and current image (which may be used in batch processing)
def imageData = getCurrentImageData()

// Create a rendered server that includes a hierarchy overlay using the current display settings
def server = new RenderedImageServer.Builder(imageData)
    .downsamples(downsample)
    .layers(new HierarchyOverlay(null, options, imageData))
    .build()

//Export the whole image
////////////////////////////////////////////////////////////////
def path2 = buildFilePath(path, imageName+".png")
writeImage(server, path2)
////////////////////////////////////////////////////////////////


// Export each annotation separately
/*
int count = 0
for (annotation in getAnnotationObjects()) {
    count++
    def imageName = imageName +"_"+ count + '.png'
    def path2 = buildFilePath(path, imageName)
    def region = RegionRequest.createInstance(server.getPath(), downsample, annotation.getROI())
    writeImageRegion(server, region, path2)
}
*/
print "Done"

import qupath.lib.gui.tools.*
import java.nio.file.Paths;
import java.nio.file.Path;
import qupath.lib.gui.prefs.PathPrefs;
import qupath.lib.color.ColorMaps
import qupath.imagej.tools.IJTools
import qupath.lib.gui.images.servers.RenderedImageServer
import qupath.lib.gui.viewer.overlays.HierarchyOverlay
import qupath.lib.regions.RegionRequest
