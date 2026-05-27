import * as cornerstone from 'cornerstone-core'
import dicomParser from 'dicom-parser'
import cornerstoneTools from 'cornerstone-tools'
import cornerstoneMath from 'cornerstone-math'
import cornerstoneWebImageLoader from './cornerstoneWebImageLoader'
import localCommonImageLoader from './localCommonImageLoader'

export default function initCornerstoneFamily() {
  /* global cornerstoneWADOImageLoader */
  cornerstoneWADOImageLoader.external.cornerstone = cornerstone
  cornerstoneWADOImageLoader.external.dicomParser = dicomParser
  cornerstoneWebImageLoader.external.cornerstone = cornerstone
  localCommonImageLoader.external.cornerstone = cornerstone
  
  cornerstoneTools.external.cornerstone = cornerstone
  cornerstoneTools.external.cornerstoneMath = cornerstoneMath

  cornerstoneTools.init({
    mouseEnabled: true,
    touchEnabled: false,
    globalToolSyncEnabled: false,
    showSVGCursors: true,
    autoResizeViewports: true,
  })
}