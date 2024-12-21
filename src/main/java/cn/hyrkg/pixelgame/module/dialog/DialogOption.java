package cn.hyrkg.pixelgame.module.dialog;

public class DialogOption {
	public final int id;
	public final String content;

	public DialogOption(int id, String content) {
		this.id = id;
		this.content = content;
	}

	public static DialogOption of(int id, String content) {
		return new DialogOption(id, content);
	}
}
