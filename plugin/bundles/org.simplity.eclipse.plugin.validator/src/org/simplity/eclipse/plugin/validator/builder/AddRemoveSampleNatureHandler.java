package org.simplity.eclipse.plugin.validator.builder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.eclipse.core.commands.*;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.ui.handlers.HandlerUtil;
import org.simplity.kernel.Application;
import org.simplity.kernel.util.ReflectUtil;
import org.simplity.kernel.util.XmlUtil;

public class AddRemoveSampleNatureHandler extends AbstractHandler {

	private ISelection selection;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			for (Iterator<?> it = ((IStructuredSelection) selection).iterator(); it
					.hasNext();) {
				Object element = it.next();
				IProject project = null;
				if (element instanceof IProject) {
					project = (IProject) element;
				} else if (element instanceof IAdaptable) {
					project = (IProject) ((IAdaptable) element)
							.getAdapter(IProject.class);
				}
				if (project != null) {
					try {
						toggleNature(project);
					} catch (CoreException e) {
						throw new ExecutionException("Failed to toggle nature", e);
					}
				}
			}
		}

		return null;
	}

	/**
	 * Toggles sample nature on a project
	 *
	 * @param project
	 *            to have sample nature added or removed
	 */
	private void toggleNature(IProject project) throws CoreException {
		IProjectDescription projectDescription = project.getDescription();
		String[] projectNature = projectDescription.getNatureIds();
		boolean projectHasMavenNature = false;
	    for(String nature : projectNature) {
	    	if(nature.contentEquals("org.eclipse.m2e.core.maven2Nature")) {
	    		projectHasMavenNature = true;
		    	break;
	    	}
	    }
	    if(projectHasMavenNature) {
	    	IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();

			for (int i = 0; i < natures.length; ++i) {
				if (SampleNature.NATURE_ID.equals(natures[i])) {
					// Remove the nature
					String[] newNatures = new String[natures.length - 1];
					System.arraycopy(natures, 0, newNatures, 0, i);
					System.arraycopy(natures, i + 1, newNatures, i, natures.length - i - 1);
					description.setNatureIds(newNatures);
					project.setDescription(description, null);
					return;
				}
			}

			// Add the nature
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = SampleNature.NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, null);
			addComponentFolderTemplate(project);
			addApplicationXml(project);
			updateMavenDependency(project);
	    }
	}

	/*
	 * Add Simplity comp folder structure to the project
	 */
	private void addComponentFolderTemplate(IProject project) throws CoreException {
		String[] folders = {"src", "src/main", "src/main/resources", "src/main/resources/comp", "src/main/resources/comp/msg", 
				"src/main/resources/comp/rec", "src/main/resources/comp/service", "src/main/resources/comp/service/tp"};
		IFolder folder = null;
		for(String folderName : folders) {
			folder = project.getFolder(folderName);
			if(!folder.exists())
				folder.create(true, true, null);
		}
	}
	
	/*
	 * Add Simplity application.xml file to the comp folder
	 */
	private void addApplicationXml(IProject project) {
		try {
			IFile file = project.getFile("src/main/resources/comp/application.xml");
			if(!file.exists())
				file.create(null, true, null);
			if (file.exists()) {
				file.refreshLocal(IResource.DEPTH_ZERO, null);
				Application application = new Application();
				Map<String, Field> applicationFields = ReflectUtil.getAllFields(application);
				String projectName = file.getProject().getName();
				Field applicationId = applicationFields.get("applicationId");
				applicationId.setAccessible(true);
				applicationId.set(application, projectName);
				String xmlText = XmlUtil.objectToXmlString(application);	
				file.setContents(new ByteArrayInputStream(xmlText.getBytes(StandardCharsets.UTF_8)),0,null);
				file.refreshLocal(IResource.DEPTH_ZERO, null);
			}
		} catch (CoreException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Add Simplity kernel dependency to the project
	 */
	private void updateMavenDependency(IProject project) {
		try {
			MavenXpp3Reader mavenReader = new MavenXpp3Reader();
			IFile pomIFile = project.getFile("pom.xml");
			pomIFile.refreshLocal(IResource.DEPTH_ZERO, null);
			File pomFile = pomIFile.getLocation().toFile();
	        Model model = mavenReader.read(new FileReader(pomFile));
	        List<Dependency> dependencies = model.getDependencies();
	        boolean kernelDependencyExist = false;
	        for(Dependency dependency : dependencies) {
	        	if(dependency.getGroupId().equals("org.simplity") && dependency.getArtifactId().equals("kernel")) {
	        		kernelDependencyExist = true;
	        	}
	        }
	        if(!kernelDependencyExist) {
	        	Dependency dependency = new Dependency();
	        	dependency.setGroupId("org.simplity");
	        	dependency.setArtifactId("kernel");
	        	dependency.setVersion("1.2.1");
	        	model.addDependency(dependency);
	        	MavenXpp3Writer mavenWriter = new MavenXpp3Writer();
		        mavenWriter.write(new FileWriter(pomFile), model);
		        pomIFile.refreshLocal(IResource.DEPTH_ZERO, null);
	        }
		} catch(Exception ex) {
		     ex.printStackTrace();
		}
	}
}