const DicomDictionary = {
  // Patient
  x00100010: {keyword: 'PatientName', vr: 'PN', vm: 1, name: "Patient's Name"},
  x00100020: {keyword: 'PatientID', vr: 'LO', vm: 1, name: "Patient's ID"},
  x00100030: {keyword: 'PatientBirthDate', vr: 'DA', vm: 1, name: "Patient's Birth Date"},
  x00101010: {keyword: 'PatientAge', vr: 'AS', vm: 1, name: "Patient's Age"},
  x00100040: {keyword: 'PatientSex', vr: 'CS', vm: 1, name: "Patient's Sex"},

  // Study
  x00080020: {keyword: 'StudyDate', vr: 'DA', vm: 1, name: "Study Date"},
  x00080030: {keyword: 'StudyTime', vr: 'TM', vm: 1, name: "Study Time"},
  x00081030: {keyword: 'StudyDescription', vr: 'LO', vm: 1, name: "Study Description"},

  // Series
  x00080021: {keyword: 'SeriesDate', vr: 'DA', vm: 1, name: "Series Date"},
  x00080031: {keyword: 'SeriesTime', vr: 'TM', vm: 1, name: "Series Time"},
  x0008103e: {keyword: 'SeriesDescription', vr: 'LO', vm: 1, name: "Series Description"},
}

class MetaDataSet {
  constructor(dataSet) {
    for (let id in DicomDictionary) {
      if (id in dataSet.elements) {
        this.getValue(id, DicomDictionary[id], dataSet)
      }
    }
  }

  getValue(id, item, dataSet) {
    const vr = item.vr
    let v = null
    if (vr == 'PN' || vr == 'LO' || vr == 'AS' || vr == 'CS') {
      v = dataSet.string(id)
    } else if (vr == 'DA') {
      v = dataSet.string(id)
      if (v && v.length > 5) {
        v = `${v.substr(0, 4)}-${v.substr(4, 2)}-${v.substr(6)}`
      }
    } else if (vr == 'TM') {
      v = dataSet.string(id)
      let pos = v.indexOf('.')
      if (pos > -1) v = v.substr(0, pos)
      if (v.length > 5) {
        v = `${v.substr(0, 2)}:${v.substr(2, 2)}:${v.substr(4)}`
      }
    }

    if (v) {
      this[item.keyword] = v
    }
  }
}

export default MetaDataSet
