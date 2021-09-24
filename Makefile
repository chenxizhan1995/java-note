serve:
	- mdbook serve --port 8081

clean:
	find -name '*.class' -print0 | xargs -0 rm -v
