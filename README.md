# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

[Server Sequence Diagram](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43Fgp0BFzKMCowGQWkykQAjj41GAABQASkwYOBJ0KR1E5SgURimSgKMi0VilARSNiGLxKhxMm0ShU6nK9hQYAAqh0UTc7hjZPIWWpVIzjKUAGJITgwHmUIXAGA6ML84CjTCKkXqE6nekocpoHwIBCHYqiRla5Si0ogQlyFDy0k3RWC5nWnWnCUKDgcOUdRVmkQM05W1mqW32zIKHxgVIo4Cx1KuzXu8PinSlH1+mNxwP63WFLGXCJEylQSKqY1YYsgvXFEvXDp3SblVZPRNx+oQADW6FbU32QeOuVO2XM5QATE4nN0m0N1WMYG3HlNO6lu320APVgdMNDmAFCX6AmkMoYAJJoKEw0uI5GY87Y0FPks3pCwlDU5Hox9AyiFkU1AlgALDOc5TKM6jABy4xTAAolA3gQnofqEvesSTAcRwAaOhTjhg5RgbOvSQaKMH3AhSHQOUqEwOhNJgFhZicKYB6BMeKTpJkMBXiSwQIEBxyIchw4WqGaY2jAHLcryaqjHS5ohi+-4QgArDO5R8Qugk4bAInQH+4KAXp5QaSRPRkdBsFtgZEIohwah2sQBCJDAEAAGYwJQyFosxemAQRYBmeBpF9FBqgUXBfR2eUDlOVALkJEknnedRUB+TABzoBwbEIIenGntxhjyYYkq+LKMpMvIegGKmwoemKXqZtKspOoqyqqs2i6aDogFhlJ7XaPViiNSZSkGv6CrDQWeFnKpRFOAAzOUTowPBAAexIaLW43ARCYFLRB4XkTZVHIeU0L2hwYTyFu-kNnW+HIBOMCHcdEVRbZ6WXQg123cA91ZSxuUg6YnjeH4gTQOwHIwAAMhA0RJEV56YEFjKmZUtQNM0LQGOoyVzqVswvG8HDYY9jK1lcAzdaMuxfGTSyMwCr4griE3lAgSMyiiiPI9+tJiSGhQDWy0mck6fL0ygboNemzXlK1fpDfInUwKVvX9ZJEtq8AI3ak19bBpN+si4YJtUPikLQh+DoCzKV4edAmyuWgv6zWLusRnaKAOrm8brim4tNYUEowNmkdJvmE2ATTCO82glbVkZz5W42dMLgzy6Dl866bv2ue7sOuFji9hEwNOFlZ7ci47qufQF72Rcrn0e7sUeKAnmePFoIkKBbZSpCle5sAyu+frM+8FuY1ztswg7SfO678SJJ7cdzQnYEAIzlLFmvBEm0BIAAXt3adl5z+2LTvH2nZRMU-Yf64n+fyzAwFc1Bbf9-WY-B8fBHzjG-buzEcp5QKt3Li55eIYB0kJACdlZ4SQVlJGS0tSqKVNvHdmIUnBaXgXXXSVNkG7Tmljcyf9IpnSfhdGA8VVDOWwO7dyXkfLQEypTfagUK7BRgFQsKn1aEH0Ycw1hqUOEZXAaxMGpgvC+H8AELwKB0CJ3sL4Q8vcsh8LnjfSo0h4Lw3gvUeCeMCaqCJsAjcLc0Clw5vNcEVxm5bjZqpPR1sVDcyRpohMSZC5oGwZ4y23s0ESxkoHPxXZbHy1GorcOmYYAqxgC49A2s5qh0uv42xSSKq5VDntYJWTolbhgAANTtrlL21U4lSUctwaMSYok2K3LEo2GZyiRGGBAGg0c8wzU3ipJx6jNEp3ypfJ6iCIQ9G4SOcuOR+HVznB3fKHFoGox4tpYhUz1rpRQaE2p4TOSRNSYElBQzgT4MIfxaEOyyHs0KSWQRlkTr-2iqIxyTDEosOSmwtKvkHo8O-nw-B1CvrnRogwz54jfmSPSlwuRndCraLgZPMeMAACyMRLEOBSdkrchsxpKySTKP0pz0moMORGPFJS1EADkIDMHKkA-JPtCk21Oaiyp5yix4Lek4Peuz6FANfold+EzHkHQFWCkRz8RXHzFWAz+VNgULN-kIh+7y5XWNAR-bKsiIHyMhkoykKAEA6AQEiBGxIYAAHFFxig2TohZHiSwVFtSYvG9hFzdHJV-XlC0YA9FOW44yVsbbIFiPaqCzSAlBPEgco27JjlNNOW0olCTlakppS0tJxgdZhOpZy5lrECkUPnpyipMJ9k1KTTASNYBo1qF-GWzNpYLCoF6dCQSTaND5oyT7coTb4bEkJYrcNXi7WLhHbEHljjLnWqjQ6sZNYHnlv0f0b1UE4IVE3YuC80g4I70nEtECTxtFOhbBMNY3Q+gWtAD2S99dPhPC3SgOli5-iNFmVfeAIKq6hT3du8ou6+hvoPUek9Z6pgXtlisG92570gEfXBl9Uw30ftGF+lZUCe7FTgTckhPDkHVMyZLMAJz8XoHjcpANwzzLXIQQFe57j10lFBRqt5316FiO+RI9h8LAVzOemqgRgGrI0IAc-XjSU3JwoBcDQ1SL1koqvGi6AmLsUykcOS1tRhEnJPJf2yldbOUMqZXksdop2WTs5Wp7ls0LmgQFfvbVoqz4X3IRnKVd9OOSa1cKnViq9X2N4aJ3eMqpOBfc+-GRoNDUKKhgEbAPgoDYG4PAKMhgm0wMyOjXR3mQM4yaK0N9vqqNoDnBhxcP6HEJ36Kcxmr7FyYfuCXchdGF2TwdELVEGIOtCRtn7B0TbY22NmNVhSNayMRNTRV9N8T9NZtlEZvqA7C3FNzUkEtrLC02cmpWhzm9r5FMlmgSgI393SA3jgkzjVyjDcyKNybKAJtXYW9Z4lcAsswDpSgZIU7RgwCqtIAAQhSxN93yA+Chzl77-tMh+hB9oWqYgy0TsmmQGH4Yh2Lkywji+jnOslnh5d0YK6JVsczuBw9bdathdeks3oYGrvRSHPuVZXc8OwPs7eXrlPicQm63CFAGE+uU8K-yiyEnwV0MhXRBiyIhO-p-lLyLAX5fADQqLxicXIFrO55sohAk7l7NI4O8jlHaVnKJ-Op5mkCNMdIXsrzJ37fS9ef57jkKZM-LkwJhT9PVWvWeTL2VPHoV8dhQHzheuOe4dy5ea8lT0VYsJri3TbLiWGYqxD2tUOzOMtySyqznoMebYCVy6ttvt4uaFZC+VIDgsS7dz59X3uISN9SLq5XkzVcRb87LwBQWPMhcNQl41gRLBmp5gDgAUhAKqOWAhIZ7Pll1kvqhchKy0Mr1iAlznS8AafUA4AQB5lAN7owD1B8F7TRraHngIGP5QM-F+mvodZ-8AXduheVJ6zrj+P1muidjbAAFaL5oCjanKkzP4n5v7QBX4oAHo0YhL5446W5zbW4faehto57W555kbFqWbo6gG2YVblJHa3aQ4YEyTPbval5hxLYkqyg5YqjSRXaEEW5NqBikGDaTo8EDLUH8GTQgCpAoDIYIFQAKBQjbC6DbAZCWA3bBK4KBoL4yhNoU6u5TJTjiYs7X604wDHqnrKpArzKM56E06QYmE4YG6J5V4fh3iMQ-4JzC5fiAGxC-jaFYzETt4QooRa70QeFMSmHCZ-rhZ6HCJRaa7a5i5x7KaG5J6Eam6iTm4baYFxhjatJzoJwMaO7bLMYu5rqS6h6e5D7SaR6yYpQx7SKhEq7-qlFREa72SVF+7VH-Kx6KayIJH2E5bmbF6lpZ54HZpsFhA06MH7aGjGimg158pgQgSub0JvowD9zMAuwl7eGPSLQgR+Fy4QjLGrEwDrGcC94M6VzzG7EHwHFF7HFj7dGc7Ir4ZH7wHn4ab9EyG-C6DcA1r6gPbiGSGvHSGyHP46AKFgBKGoGqHDLETlDPEAQgCAkrFF4hCfEWpiCbH6K+GD7h4xFBFxF1F97-pYkvJNEd60SBGK6YRdHxayKJZKJeDH5-r+ywDADYDpaEBrwozaLr7mCuoQgVCGLGKmJ4zGChZObTKhrPhkGiHcB4CKio7KEJroFSQgCylQDyn6AoAtpDHMGqnMnojlCIDMkpImhGDaBHHeDDBmk1SalcHpFY6NQTFsY2wOnhg5F8pGlyko6alaHFGt66EWRB7mGVxM4zL65c72FbIm6FGpHHbKkSx6lek2kGCKm0a-5XL5HRnO6iQYnsZiYe6kn+FxStH8YdG1FBkiYh6RGapklQoJRVF-JSIIoQIQJAA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
