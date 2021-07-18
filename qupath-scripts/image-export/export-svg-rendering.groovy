
import qupath.lib.extension.svg.SvgTools.SvgBuilder

def imageData = getCurrentImageData()
def options = getCurrentViewer().getOverlayOptions()

def doc = new SvgBuilder()
    .imageData(imageData)
    .options(options)
    .downsample(1) // Increase if needed
    .createDocument()
    
def name = GeneralTools.getNameWithoutExtension(getProjectEntry().getImageName())
def path = buildFilePath(PROJECT_BASE_DIR, name + '.svg')

new File(path).text = doc
