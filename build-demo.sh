quarkus build -Dquarkus.profile=demo && quarkus image build \
  -Dquarkus.container-image.name=recipes-quarkus \
  -Dquarkus.container-image.tag=demo \
  -Dquarkus.container-image.registry=local \
  -Dquarkus.container-image.group=default \
  -Dquarkus.profile=demo