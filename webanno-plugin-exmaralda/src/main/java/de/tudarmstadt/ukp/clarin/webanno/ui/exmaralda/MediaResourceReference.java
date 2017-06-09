package de.tudarmstadt.ukp.clarin.webanno.ui.exmaralda;

import java.io.File;
import java.io.IOException;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.IResource;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.spring.injection.annot.SpringBean;

import de.tudarmstadt.ukp.clarin.webanno.api.MediaService;
import de.tudarmstadt.ukp.clarin.webanno.api.ProjectInitializationService;
import de.tudarmstadt.ukp.clarin.webanno.model.Mediaresource;


public class MediaResourceReference extends ResourceReference{
	
	private static final long serialVersionUID = 5524878158076213156L;
	
    private @SpringBean MediaService mediaService;
    private @SpringBean ProjectInitializationService p;

	public MediaResourceReference() {
		super(MediaResourceReference.class, "webanno");
	}
	
	@Override
	public IResource getResource() {
				
		MediaResourceStreamResource r = new MediaResourceStreamResource() {

			private static final long serialVersionUID = 2888522092626490637L;

			@Override
			public Mediaresource getMediaresource(PageParameters params) {
				final long pid = params.get(MediaResourceStreamResource.PAGE_PARAM_PROJECT_ID).toLong();
				final long fid = params.get(MediaResourceStreamResource.PAGE_PARAM_FILE_ID).toLong();
				return mediaService.getMedia(pid, fid);
			}

			@Override
			public File getMediaFile(Mediaresource media) throws IOException {
				return mediaService.getFile(media);
			}
		};
		
		return r; 
		
		
		
//		MediaResourceStreamResource r = new MediaResourceStreamResource(){
//			
//			private static final long serialVersionUID = -1649133598549016083L;
//			
//			@Override
//			protected ResourceResponse newResourceResponse(Attributes attributes) {
//				ResourceResponse r = super.newResourceResponse(attributes);
//				if(attributes.getParameters().getPosition("dl") >= 0)
//					r.setContentDisposition(ContentDisposition.ATTACHMENT);
//				return r;
//			}
//
//			@Override
//			public Mediaresource getMediaresource(PageParameters params){
//				final long pid = params.get(PAGE_PARAM_PROJECT_ID).toLong();
//				final long fid = params.get(PAGE_PARAM_FILE_ID).toLong();
//				return mediaService.getMedia(pid, fid);
//			}
//			
//			@Override
//			public File getFile(Mediaresource mfile) throws IOException {
//				return mediaService.getFile(mfile);
//			}
//			
//		};
//		
//		return r; 
		

//		final long pid = 1;//params.get(PAGE_PARAM_PROJECT_ID).toLong();
//		final long fid = 1;//params.get(PAGE_PARAM_FILE_ID).toLong();
//		
//		final Mediaresource mfile = mediaService.getMediafile(pid, fid);
//		File file;
//		try {
//			file = mediaService.getFile(mfile);
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		}
//		return new ResourceStreamResource(new FileResourceStream(file){
//			private static final long serialVersionUID = 8592122044512919133L;
//			@Override
//			public String getContentType() {
//				return mfile.getContentType();
//			}
//		});
	}
	

		
//		@Override
//		public IResource getResource() {
//		
//		return new AbstractResource() {
//
//			private static final long serialVersionUID = 204014928360078510L;
//
//			@Override
//			protected ResourceResponse newResourceResponse(Attributes attributes) {
//				
//				ResourceResponse response = new ResourceResponse();
//				response.setLastModified(Time.now());
//				
//				PageParameters params = attributes.getParameters();
//				if(params == null){
//					LOG.warn("No parameters provided.");
//					response.setError(400);
//					return response;
//				}
//				
//				if (response.dataNeedsToBeWritten(attributes))
//				{
//					response.setContentDisposition(ContentDisposition.INLINE);
////					response.setContentDisposition(ContentDisposition.ATTACHMENT);
//				
//				
//					long pid = params.get(PAGE_PARAM_PROJECT_ID).toLong();
//					long fid = params.get(PAGE_PARAM_FILE_ID).toLong();
//					
//					Mediafile mfile = mediaService.getMediafile(pid, fid);
//					
//					response.setContentType(mfile.getContentType());
//					response.setContentLength(mfile.getContentLength());
//					response.setFileName(mfile.getName());
//					
//					byte[] bytes = null;
//					InputStream in = null;
////						OutputStream out = null;
//			        try {
//			        	in = mediaService.getContentAsInputStream(mfile);
////				        	out = attributes.getResponse().getOutputStream();
//			        	
//			        	bytes = IOUtils.toByteArray(in);
//
//			        	in.close();
////				        	IOUtils.copy(in, out);
////				            copyLarge(in, out);
//			        }catch(Throwable e){
//			        	LOG.error(e.getMessage());
//			        	response.setError(500);
//						return response;
//			        }
//			        finally {
////				            closeQuietly(out);
//			            closeQuietly(in);
//			        }
//
//					final byte[] data = bytes;
//					
//					response.setWriteCallback(new WriteCallback() {
//						@Override
//						public void writeData(Attributes attributes) throws IOException {
//							attributes.getResponse().write(data);
//						}
//					});
//
////					configureResponse(response, attributes);
//					
//				}
////				resourceResponse.setStatusCode(200);
////				respond(attributes);
//
//				return response;
//			}
//		};
//		
//	};

}






