# Személyi Igazolvány Igénylés Szimuláció

Ez a repository a személyi igazolvány igénylés szimulációjának automatizálására készült. A program Maven használatával készült, és a **Spring Boot** keretrendszerre épül. A BPMN folyamatok kezelésére a Camunda platformot használja, és a megadott folyamatdiagramot dolgozza fel.

## Előfeltételek

A program használatához szükséges a Camunda környezet futtatása. Részletes dokumentáció a Camunda telepítéséről és beállításáról az alábbi linkeken érhető el:
- [Camunda Self-Managed telepítés Dockerrel](https://docs.camunda.io/docs/self-managed/setup/deploy/other/docker/)
- [Camunda Docker Compose konfiguráció](https://github.com/camunda/camunda-platform/blob/main/docker-compose.yaml)

## BPMN Diagram

A program alapértelmezés szerint a `src/main/resources/SzigKer.bpmn` fájlt használja, amely a folyamatábrát tartalmazza. Más BPMN diagramok szintén megadhatók ugyanebben a mappában, azonban nem lett tesztelve, hogy több diagram esetén melyik kerül futtatásra.

## Konfiguráció

A `src/main/resources/application.properties` fájlban adható meg a Zeebe portbeállítása. Példa:

```properties
zeebe.client.broker.gateway-address=127.0.0.1:26500
