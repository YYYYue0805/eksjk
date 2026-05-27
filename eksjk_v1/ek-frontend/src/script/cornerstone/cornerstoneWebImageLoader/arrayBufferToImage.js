/**
 * Convert array buffer to image. Returns a promise that resolves to an Image object for the bytes in arrayBuffer
 *
 * @param arrayBuffer - arrayBuffer with bytes for a web image (e.g. JPEG, PNG, etc)
 * @returns {Promise} Promise that resolves to an Image object
 */

import UTIF from 'utif'

export default function (arrayBuffer, isTiff) {
  console.log('is tiff=', isTiff)
  return new Promise((resolve, reject) => {
    let imageUrl = ''
    const urlCreator = window.URL || window.webkitURL;

    if (isTiff) {
      let ifds = UTIF.decode(arrayBuffer);
      const ifd = ifds[0];
      UTIF.decodeImage(arrayBuffer, ifd);
      const rgba  = UTIF.toRGBA8(ifd);
i
      var canvas = document.createElement('canvas');
      canvas.width = ifd.width;
      canvas.height= ifd.height;
      var ctx = canvas.getContext('2d');
      var imageData = ctx.createImageData(ifd.width, ifd.height);

      for (var i=0; i<imageData.data.length; i++) {
        imageData.data[i] = rgba[i];
      }

      ctx.putImageData(imageData, 0, 0);
      imageUrl = canvas.toDataURL('image/png')
    } else {
      const arrayBufferView = new Uint8Array(arrayBuffer);
      const blob = new Blob([arrayBufferView]);
      imageUrl = urlCreator.createObjectURL(blob);
    }

    const image = new Image();
    image.src = imageUrl;
    image.onload = () => {
      resolve(image);
      urlCreator.revokeObjectURL(imageUrl);
    };

    image.onerror = (error) => {
      urlCreator.revokeObjectURL(imageUrl);
      reject(error);
    };
  });
}
