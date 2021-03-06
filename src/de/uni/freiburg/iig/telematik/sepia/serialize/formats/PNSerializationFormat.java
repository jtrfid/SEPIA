package de.uni.freiburg.iig.telematik.sepia.serialize.formats;

import de.invation.code.toval.file.FileFormat;


public enum PNSerializationFormat {
	
	PNML(new PNFF_PNML()),
	
	PETRIFY(new PNFF_Petrify());
	
	private FileFormat fileFormat = null;
	
	private PNSerializationFormat(FileFormat fileFormat){
		this.fileFormat = fileFormat;
	}
	
	public FileFormat getFileFormat(){
		return fileFormat;
	}

}
