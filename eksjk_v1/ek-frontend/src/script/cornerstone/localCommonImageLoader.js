let cornerstone
const canvas = document.createElement('canvas')

function createImage(image, imageId) {
  const rows = image.naturalHeight
  const columns = image.naturalWidth

  function getImageData () {
    canvas.height = image.naturalHeight
    canvas.width = image.naturalWidth
    let context = canvas.getContext('2d')
    context.drawImage(image, 0, 0)

    return context.getImageData(0, 0, image.naturalWidth, image.naturalHeight)
  }

  function getPixelData () {
    const imageData = getImageData()
    return imageData.data
  }

  function getCanvas () {
    canvas.height = image.naturalHeight
    canvas.width = image.naturalWidth
    const context = canvas.getContext('2d')
    context.drawImage(image, 0, 0)
    return canvas
  }

  return {
    imageId,
    minPixelValue: 0,
    maxPixelValue: 255,
    slope: 1,
    intercept: 0,
    windowCenter: 128,
    windowWidth: 255,
    render: cornerstone.renderWebImage,
    getPixelData,
    getCanvas,
    getImage: () => image,
    rows,
    columns,
    height: rows,
    width: columns,
    color: true,
    rgba: false,
    columnPixelSpacing: undefined,
    rowPixelSpacing: undefined,
    invert: false,
    sizeInBytes: rows * columns * 4
  }
}

function loadImage(imageId) {
  const promise = new Promise((resolve) => {
    let url = imageId.substring('localCommon:'.length)

    const image = new Image()
    image.onload = () => {
      resolve(createImage(image, imageId))
    }
    image.src = url
  })

  return {
    promise,
    cancelFn: undefined
  }
}

const external = {
  set cornerstone (v) {
    cornerstone = v

    cornerstone.registerImageLoader('localCommon', loadImage)
  },
  get cornerstone () {
    return cornerstone
  }
}

const localCommonImageLoader = {
  external
}

export default localCommonImageLoader
