package uk.ac.jisc.monitorlocal

import java.sql.Blob
import org.hibernate.Session


class FileAttachment {

  private static final MAX_SIZE = 1073741824

  String filename
  Component owner
  Blob blobContent
  Date dateCreated
  Date lastUpdated


  static mapping = {
    blobContent column:'doc_blob_content'
  }

  static constraints = {
    owner nullable: false
    filename(nullable:true, blank:true)
    dateCreated(nullable:true, blank:true)
    lastUpdated(nullable:true, blank:true)
    blobContent(nullable:true, blank:false, maxSize:MAX_SIZE)
  }

  def setBlobData(InputStream is, long length) {
    Session hib_ses = sessionFactory.getCurrentSession()
    blobContent = hib_ses.getLobHelper().createBlob(is, length)
  }

  def getBlobData() {
    return blobContent?.binaryStream
  }

  Long getBlobSize() {
    return blobContent?.length() ?: 0
  }

  def render(def response) {
    response.setContentType(mimeType)
    response.addHeader("content-disposition", "attachment; filename=\"${filename}\"")
    response.outputStream << getBlobData()
  }

  static fromUpload(def file) {
    if(!file) return new FileAttachment()

    def filename = file.originalFilename
    def slashIndex = Math.max(filename.lastIndexOf("/"),filename.lastIndexOf("\\"))
    if(slashIndex > -1) filename = filename.substring(slashIndex + 1)

    def doc = new FileAttachment(filename: filename)
    doc.setBlobData(file.inputStream, file.size)
    return doc
  }

}
