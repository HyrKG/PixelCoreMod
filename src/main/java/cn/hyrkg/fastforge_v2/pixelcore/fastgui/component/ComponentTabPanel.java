package cn.hyrkg.fastforge_v2.pixelcore.fastgui.component;

/**
 * 你可以用这个面板创建诸多二级面板
 */
public abstract class ComponentTabPanel extends BaseComponent {

	private BaseComponent oldComponent = null;

	public void switchTo(BaseComponent newComponent) {
		if (this.oldComponent != null)
			this.removeComponent(newComponent);
		this.addComponent(newComponent);
	}
}
