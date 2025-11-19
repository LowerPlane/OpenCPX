from setuptools import setup, find_packages

setup(
    name="opencpx",
    version="1.0.0",
    description="OpenCPX SDK for Python - Compliance Posture eXchange",
    long_description=open("README.md").read(),
    long_description_content_type="text/markdown",
    author="OpenCPX Working Group",
    author_email="info@opencpx.io",
    url="https://github.com/opencpx/sdk-python",
    packages=find_packages(),
    python_requires=">=3.9",
    extras_require={
        "flask": ["flask>=2.0"],
        "fastapi": ["fastapi>=0.68", "uvicorn>=0.15"],
        "django": ["django>=3.2"],
    },
    classifiers=[
        "Development Status :: 4 - Beta",
        "Intended Audience :: Developers",
        "License :: OSI Approved :: MIT License",
        "Programming Language :: Python :: 3",
        "Programming Language :: Python :: 3.9",
        "Programming Language :: Python :: 3.10",
        "Programming Language :: Python :: 3.11",
        "Programming Language :: Python :: 3.12",
    ],
)
