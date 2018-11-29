---
title: Générateur de formes - Définition de la classe de forme
authors:
    - Mickaël Brasebin
date: 2018-10-26

---

# Paramétrer les boîtes d'une configuration

Dans cette partie, nous décrivons comment le système définit la création de nouveaux objets paramétriques et les modifications qui leur seront apportées durant l'optimisation. Il s'agit de l'étape 1 du code décrit dans la section [principe de simulation](principe.md). Cet exemple s'appuie sur la génération de boîtes, mais il est possible de définir d'autres types de géométries (cela sera décrit dans la section [Générer d'autres types de formes](custom-shape.md)).

Les parties suivantes reprennent les principales étapes de création du sampler dont le code est repris dans [la dernière partie de la page sur l'implémentation](#implementation) (étape 1) , c'est à dire le code suivant :


```JAVA
//Step 1 :

// Sampler creation (definition of the class and of the kernel modifications)
// Création de l'échantilloneeur (définition de la classe et des noyaux de modifications)
Sampler<GraphConfiguration<Cuboid>, BirthDeathModification<Cuboid>> samp = create_sampler(Random.random(), p,
bpu, pred, geom);
```

# Définition des formes générables


Pour rappel, le processus d'optimisation vise à générer une configuration composée de *n* boîtes chaque boîte pouvant être définie par un vecteur de dimension 6 :  
 **b** = (**x**, **y**, **l**, **w**, **h**, **θ**).

Afin de pouvoir échantillonner, il est nécessaire de définir les intervalles dans lesquels le tirage des boîtes sera effectué :

- **xmin** < **x** < **xmax**
- **ymin** < **y** < **ymax**
- **minθ** < **θ** < **maxθ**
- **minwid** < **w** < **maxwid**
- **minlen** < **l** < **maxlen**
- **minheight** < **h** < **maxheight**

Les deux premières valeurs sont contraintes par l'enveloppe contenant la géométrie dans laquelle les centres de boîtes seront échantillonnés.

Dans l'exemple de la [première simulation](../begin/first_simulation.md), **minθ** et **maxθ** sont fixés  à **0** et **π** afin de permettre tout type d'orientation. Les autres paramètres sont fixés dans le fichier de configuration **params.json**.

Afin que le système puisse générer des formes, il est nécessaire de définir une classe **Builder** qui permet d'instancier les objets à partir d'un tableau contenant les paramètres de la forme. Dans l'exemple, il s'agit de la classe **CuboidBuilder**.

# Définition des modifications

Une modification aléatoire est est déterminée par le choix d’un des noyaux de proposition. Chaque noyau a la même probabilité d'être sélectionné. Dans le cadre de l'exemple, 6 noyaux sont utilisés :

- ajout/suppression d’une nouvelle boîte ;
- translation d’une boîte ;
- changement de la longueur d’une boîte ;
- changement de la largeur d’une boîte ;
- changement de la hauteur d’une boîte ;
- rotation d’une boîte.

Exceptés les modifications issues du premier noyau, toutes les modifications sont implémentées comme la suppression et l'ajout d'une boîte. Par exemple, une modification de type changement de longueur d'une boîte **b** = (**x**, **y**, **l**, **w**, **h**, **θ**) est traduite comme la suppression de cette boîte et la création d'une nouvelle boîte avec une longueur différente, définie comme : **b'** = (**x**, **y**, **l** + (**1 - rand**)  x **ampli**, **w**, **h**, **θ**) où **rand** est un nombre aléatoire pris dans [0,1] et **ampli** est un coefficient d'amplification du déplacement (réel strictement positif).
Ainsi, dans la simulation exemple, les coefficients des amplifications (*amplitudeMove*, *amplitudeMaxDim*, *amplitudeHeight*, *amplitudeRotate*) sont stockés dans le fichier de paramétrage **params.json**.

Les modifications sont implémentées à partir de la classe **Kernel** de la librjmcmc4j qui définit comment modifier un objet de la classe paramétrique utilisée à partir d'un tirage aléatoire.

 # Création de l'échantillonneur de Green

Les dernières étapes visent à définir l'objet qui va effectuer les tirages aléatoires des boîtes qui seront créées (classe **DirectSampler** de librjmcmc4j) et l'échantillonneur de Green qui va définir les modifications à appliquer pendant la simulation et notamment la probabilité d'acception durant le process.

 # Implémentation


```JAVA

	/**
	 * Creation of the sampler
	 * @param rng  a random generator
	 * @param p    the parameters loaded from the json file
	 * @param bpU  the basic property unit on which the simulation will be proceeded
	 * @param pred a predicate that will check the respect of the rules
	 * @param geom a geometry that will contains all the cuboid
	 * @return a sampler that will be used during the simulation process
	 */
	public Sampler<GraphConfiguration<Cuboid>, BirthDeathModification<Cuboid>> create_sampler(RandomGenerator rng,
			SimpluParameters p, BasicPropertyUnit bpU,
			ConfigurationModificationPredicate<GraphConfiguration<Cuboid>, BirthDeathModification<Cuboid>> pred,
			IGeometry geom) {


		//Step 1 : Creation of the object that will control the birth and death of cuboid

		//Getting minimal and maximal dimension from the parameter file
		double minlen = Double.isNaN(this.minLengthBox) ? p.getDouble("minlen") : this.minLengthBox;
		double maxlen = Double.isNaN(this.maxLengthBox) ? p.getDouble("maxlen") : this.maxLengthBox;

		double minwid = Double.isNaN(this.minWidthBox) ? p.getDouble("minwid") : this.minWidthBox;
		double maxwid = Double.isNaN(this.maxWidthBox) ? p.getDouble("maxwid") : this.maxWidthBox;

		double minheight = p.getDouble("minheight");
		double maxheight = p.getDouble("maxheight");


		//Builder class of the object
		ObjectBuilder<Cuboid> builder = new CuboidBuilder();

		//The geometry in which the sampler will be instanciated
		if (geom != null) {
			samplingSurface = geom;
		}

		if (samplingSurface == null) {
			samplingSurface = bpU.getGeom();
		}
		IEnvelope env = samplingSurface.getEnvelope();

		//Instanciation of the object dedicated for the creation of new cuboid during the process
		//Passing the building, the class (TransformToSurface) that will make
		// the transformation between random numbers and coordinates inside the samplingSurface
		UniformBirth<Cuboid> birth = new UniformBirth<Cuboid>(rng,
				new Cuboid(env.minX(), env.minY(), minlen, minwid, minheight, 0),
				new Cuboid(env.maxX(), env.maxY(), maxlen, maxwid, maxheight, Math.PI), builder,
				TransformToSurface.class, samplingSurface);



		//Step 2  : Listing the modification kernel

		//List of kernel for modification during the process
		List<Kernel<GraphConfiguration<Cuboid>, BirthDeathModification<Cuboid>>> kernels = new ArrayList<>();

		//A factory to create proper kernels
		KernelFactory<Cuboid, GraphConfiguration<Cuboid>, BirthDeathModification<Cuboid>> factory = new KernelFactory<>();

		//Adding the birth/death kernel
		kernels.add(
				factory.make_uniform_birth_death_kernel(rng, builder, birth, p.getDouble("pbirth"), 1.0, "BirthDeath"));
		//Adding the other modification kernel
		double amplitudeMove = p.getDouble("amplitudeMove");
		kernels.add(factory.make_uniform_modification_kernel(rng, builder, new MoveCuboid(amplitudeMove), 0.2, "Move"));
		double amplitudeRotate = p.getDouble("amplitudeRotate") * Math.PI / 180;
		kernels.add(factory.make_uniform_modification_kernel(rng, builder, new RotateCuboid(amplitudeRotate), 0.2,
				"Rotate"));
		double amplitudeMaxDim = p.getDouble("amplitudeMaxDim");
		kernels.add(factory.make_uniform_modification_kernel(rng, builder, new ChangeWidth(amplitudeMaxDim), 0.2,
				"ChgWidth"));
		kernels.add(factory.make_uniform_modification_kernel(rng, builder, new ChangeLength(amplitudeMaxDim), 0.2,
				"ChgLength"));
		double amplitudeHeight = p.getDouble("amplitudeHeight");
		kernels.add(factory.make_uniform_modification_kernel(rng, builder, new ChangeHeight(amplitudeHeight), 0.2,
				"ChgHeight"));

		//Step 3  : Creation of the sampler for the brith/death of cuboid

		// This distribution create a biais to make the system tends around a certain number of boxes
		PoissonDistribution distribution = new PoissonDistribution(rng, p.getDouble("poisson"));
		//Creation of the sampler with the modification in itself
		DirectSampler<Cuboid, GraphConfiguration<Cuboid>, BirthDeathModification<Cuboid>> ds = new DirectRejectionSampler<>(
				distribution, birth, pred);

		//Step 4  : Creation of the GreenSampler that will be used during the optimization process
		//It notably control the acception ratio and that the created objects and that the proposed configurations area generated
		//According to the uniformbirth
		Sampler<GraphConfiguration<Cuboid>, BirthDeathModification<Cuboid>> s = new GreenSamplerBlockTemperature<>(rng,
				ds, new MetropolisAcceptance<SimpleTemperature>(), kernels);
		return s;
	}
  ```
