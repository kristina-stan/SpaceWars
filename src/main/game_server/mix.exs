defmodule SpaceGame.MixProject do
  use Mix.Project

  def project do
    [
      app: :space_game,
      version: "0.1.0",
      elixir: "~> 1.14",
      start_permanent: Mix.env() == :prod,
      deps: deps()
    ]
  end

  def application do
    [
      extra_applications: [:logger],
      mod: {SpaceGame.Application, []}
    ]
  end

  defp deps do
    [
      {:jason, "~> 1.4"},
      {:ranch, "~> 2.1"}
    ]
  end
end
