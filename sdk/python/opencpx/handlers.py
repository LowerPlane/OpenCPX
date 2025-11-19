"""OpenCPX HTTP handlers for Flask and FastAPI"""

from typing import Callable
from .models import Posture


def create_flask_handler(provider: Callable[[], Posture]):
    """
    Create a Flask route handler for the /cpx endpoint.

    Args:
        provider: A function that returns a Posture object

    Returns:
        A Flask view function

    Example:
        from flask import Flask
        from opencpx import create_flask_handler, Posture, Framework, FrameworkStatus

        app = Flask(__name__)

        def get_posture():
            posture = Posture()
            posture.add_framework(Framework("SOC2", FrameworkStatus.COMPLIANT, 1.0))
            return posture

        app.route('/cpx')(create_flask_handler(get_posture))
    """
    def handler():
        try:
            from flask import jsonify, request
        except ImportError:
            raise ImportError("Flask is required. Install with: pip install flask")

        posture = provider()
        response = jsonify(posture.to_dict())
        response.headers["X-CPX-Version"] = "v1"
        return response

    return handler


def create_fastapi_router(provider: Callable[[], Posture]):
    """
    Create a FastAPI router with the /cpx endpoint.

    Args:
        provider: A function that returns a Posture object

    Returns:
        A FastAPI APIRouter

    Example:
        from fastapi import FastAPI
        from opencpx import create_fastapi_router, Posture, Framework, FrameworkStatus

        app = FastAPI()

        def get_posture():
            posture = Posture()
            posture.add_framework(Framework("SOC2", FrameworkStatus.COMPLIANT, 1.0))
            return posture

        router = create_fastapi_router(get_posture)
        app.include_router(router)
    """
    try:
        from fastapi import APIRouter
        from fastapi.responses import JSONResponse
    except ImportError:
        raise ImportError("FastAPI is required. Install with: pip install fastapi")

    router = APIRouter()

    @router.get("/cpx")
    async def cpx_endpoint():
        posture = provider()
        return JSONResponse(
            content=posture.to_dict(),
            headers={"X-CPX-Version": "v1"}
        )

    return router


def create_django_view(provider: Callable[[], Posture]):
    """
    Create a Django view for the /cpx endpoint.

    Args:
        provider: A function that returns a Posture object

    Returns:
        A Django view function

    Example:
        from django.urls import path
        from opencpx import create_django_view, Posture, Framework, FrameworkStatus

        def get_posture():
            posture = Posture()
            posture.add_framework(Framework("SOC2", FrameworkStatus.COMPLIANT, 1.0))
            return posture

        urlpatterns = [
            path('cpx', create_django_view(get_posture)),
        ]
    """
    def view(request):
        try:
            from django.http import JsonResponse
        except ImportError:
            raise ImportError("Django is required. Install with: pip install django")

        posture = provider()
        response = JsonResponse(posture.to_dict())
        response["X-CPX-Version"] = "v1"
        return response

    return view
